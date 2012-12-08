/*
 * Copyright 2009 Sven Meier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicketdnd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.Request;

import wicketdnd.util.CollectionFormattable;
import wicketdnd.util.MarkupIdVisitor;

/**
 * A target of drops. Can be configured for specific {@link Location}s via CSS
 * selectors.
 * 
 * @see #getTypes()
 * @see #onDrag(AjaxRequestTarget, Location)
 * @see #onDrop(AjaxRequestTarget, Transfer, Location)
 * 
 * @author Sven Meier
 */
public class DropTarget extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private String centerSelector = Transfer.UNDEFINED;

	private String topSelector = Transfer.UNDEFINED;

	private String bottomSelector = Transfer.UNDEFINED;

	private String leftSelector = Transfer.UNDEFINED;

	private String rightSelector = Transfer.UNDEFINED;

	private Set<Operation> operations;

	/**
	 * Create a target for drop.
	 * 
	 * @param operations
	 *            allowed operations
	 * 
	 * @see #getOperations()
	 */
	public DropTarget(Operation... operations)
	{
		this(Operation.of(operations));
	}

	/**
	 * Create a target for drop.
	 * 
	 * @param operations
	 *            allowed operations
	 * 
	 * @see #getOperations()
	 */
	public DropTarget(Set<Operation> operations)
	{
		this.operations = operations;
	}

	/**
	 * Get possible types for a transfer.
	 * 
	 * @return transfers
	 * @see Transfer#getType()
	 */
	public String[] getTypes()
	{
		return new String[] { Transfer.ANY };
	}

	/**
	 * Allow drop on the {@link Anchor#CENTER} of elements matching the given
	 * selector.
	 * 
	 * Make sure all matching elements are configured to output their markup id.
	 * 
	 * @param selector
	 *            element selector
	 * @see Component#setOutputMarkupId(boolean)
	 */
	public DropTarget dropCenter(String selector)
	{
		this.centerSelector = selector;
		return this;
	}

	/**
	 * Allow drop on the {@link Anchor#TOP} of elements matching the given
	 * selector.
	 * 
	 * Make sure all matching elements are configured to output their markup id.
	 * 
	 * @param selector
	 *            element selector
	 * @see Component#setOutputMarkupId(boolean)
	 */
	public DropTarget dropTop(String selector)
	{
		this.topSelector = selector;
		return this;
	}

	/**
	 * Allow drop on the {@link Anchor#RIGHT} of elements matching the given
	 * selector.
	 * 
	 * Make sure all matching elements are configured to output their markup id.
	 * 
	 * @param selector
	 *            element selector
	 * @see Component#setOutputMarkupId(boolean)
	 */
	public DropTarget dropRight(String selector)
	{
		this.rightSelector = selector;
		return this;
	}

	/**
	 * Allow drop on the {@link Anchor#BOTTOM} of elements matching the given
	 * selector.
	 * 
	 * Make sure all matching elements are configured to output their markup id.
	 * 
	 * @param selector
	 *            element selector
	 * @see Component#setOutputMarkupId(boolean)
	 */
	public DropTarget dropBottom(String selector)
	{
		this.bottomSelector = selector;
		return this;
	}

	/**
	 * Allow drop on the {@link Anchor#LEFT} of elements matching the given
	 * selector.
	 * 
	 * Make sure all matching elements are configured to output their markup id.
	 * 
	 * @param selector
	 *            element selector
	 * @see Component#setOutputMarkupId(boolean)
	 */
	public DropTarget dropLeft(String selector)
	{
		this.leftSelector = selector;
		return this;
	}

	/**
	 * @see #dropTop(String)
	 * @see #dropBottom(String)
	 */
	public DropTarget dropTopAndBottom(String selector)
	{
		this.topSelector = selector;
		this.bottomSelector = selector;
		return this;
	}

	/**
	 * @see #dropLeft(String)
	 * @see #dropRight(String)
	 */
	public DropTarget dropLeftAndRight(String selector)
	{
		this.leftSelector = selector;
		this.rightSelector = selector;
		return this;
	}

	@Override
	public final void renderHead(Component component, IHeaderResponse response)
	{
		super.renderHead(component, response);

		renderDropHead(response);
	}

	private void renderDropHead(IHeaderResponse response)
	{
		response.render(JavaScriptHeaderItem.forReference(Transfer.JS));

		final String id = getComponent().getMarkupId();
		String initJS = String.format(
				"new wicketdnd.dropTarget('%s','%s',%s,%s,{'center':'%s','top':'%s','right':'%s','bottom':'%s','left':'%s'});", id,
				getCallbackUrl(), new CollectionFormattable(getOperations()),
				new CollectionFormattable(getTypes()), centerSelector, topSelector, rightSelector,
				bottomSelector, leftSelector);
		response.render(OnDomReadyHeaderItem.forScript(initJS));
	}

	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		
		tag.append("class", "dnd-drop-target", " ");
	}
	
	/**
	 * Get supported operations.
	 * 
	 * @return operations
	 * @see Transfer#getOperation()
	 */
	public Set<Operation> getOperations()
	{
		return operations;
	}

	@Override
	protected final void respond(AjaxRequestTarget target)
	{
		Request request = getComponent().getRequest();

		final String phase = readPhase(request);

		final Location location = readLocation(request);

		if ("drag".equals(phase))
		{
			onDrag(target, location);
		}
		else if ("drop".equals(phase))
		{
			try
			{
				final DragSource source = DragSource.read(getComponent().getPage(), request);

				final Transfer transfer = readTransfer(request, source);

				source.beforeDrop(request, transfer);

				onDrop(target, transfer, location);

				source.afterDrop(target, transfer);
			}
			catch (Reject reject)
			{
				onRejected(target);
			}
		}
		else
		{
			throw new WicketRuntimeException("unkown type '" + phase + "'");
		}
	}

	private String readPhase(Request request)
	{
		return request.getRequestParameters().getParameterValue("phase").toString();
	}

	private Transfer readTransfer(Request request, DragSource source)
	{
		Operation operation = Operation.valueOf(request.getRequestParameters().getParameterValue(
				"operation").toString());

		if (!hasOperation(operation) || !source.hasOperation(operation))
		{
			throw new Reject();
		}

		List<String> transfers = new ArrayList<String>();
		for (String transfer : this.getTypes())
		{
			transfers.add(transfer);
		}
		transfers.retainAll(Arrays.asList(source.getTypes()));
		if (transfers.size() == 0)
		{
			throw new Reject();
		}

		return new Transfer(transfers.get(0), operation);
	}

	final boolean hasOperation(Operation operation)
	{
		return getOperations().contains(operation);
	}

	private Location readLocation(Request request)
	{
		String id = getComponent().getRequest().getRequestParameters().getParameterValue(
				"component").toString();

		Component component = MarkupIdVisitor.getComponent((MarkupContainer)getComponent(), id);

		Anchor anchor = Anchor.valueOf(request.getRequestParameters().getParameterValue("anchor")
				.toString());

		return new Location(component, anchor);
	}

	/**
	 * Notification that a drag happend over this drop target.
	 * 
	 * @param target
	 *            initiating request target
	 * @param location
	 *            the location
	 */
	public void onDrag(AjaxRequestTarget target, Location location)
	{
	}

	/**
	 * Notification that a drop happend on this drop target.
	 * 
	 * The default implementation always rejects the drop.
	 * 
	 * @param target
	 *            initiating request target
	 * @param transfer
	 *            the transfer
	 * @param location
	 *            the location
	 * @throws Reject
	 *             may reject the drop
	 */
	public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location)
			throws Reject
	{
		transfer.reject();
	}

	/**
	 * Hook method to handle rejected drops. Default implementation does
	 * nothing.
	 * 
	 * @param target
	 *            initiating request target
	 */
	public void onRejected(AjaxRequestTarget target)
	{
	}
}