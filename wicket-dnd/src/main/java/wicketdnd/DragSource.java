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

import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.Request;

import wicketdnd.util.CollectionFormattable;
import wicketdnd.util.MarkupIdVisitor;

/**
 * A source of drags.
 * 
 * @see #getTypes()
 * @see #onBeforeDrop(Component, Transfer)
 * @see #onAfterDrop(AjaxRequestTarget, Transfer)
 * 
 * @author Sven Meier
 */
public class DragSource extends Behavior
{

	private static final long serialVersionUID = 1L;

	private Component component;

	private String selector = Transfer.UNDEFINED;

	private String initiateSelector = Transfer.UNDEFINED;

	private String cloneSelector = Transfer.UNDEFINED;

	private Set<Operation> operations;

	/**
	 * Create a source of drags.
	 * 
	 * @param operations
	 *            allowed operations
	 * 
	 * @see #getOperations()
	 */
	public DragSource(Operation... operations)
	{
		this(Operation.of(operations));
	}

	/**
	 * Create a source of drags.
	 * 
	 * @param operations
	 *            allowed operations
	 * 
	 * @see #getOperations()
	 */
	public DragSource(Set<Operation> operations)
	{
		this.operations = operations;
	}

	/**
	 * Get supported types for a transfer.
	 * 
	 * @return transfers
	 * @see Transfer#getType()
	 */
	public String[] getTypes()
	{
		return new String[] { Transfer.ANY };
	}

	/**
	 * Allow drag on elements matching the given selector.
	 * 
	 * Make sure all matching elements are configured to output their markup id.
	 * 
	 * @param selector
	 *            element selector
	 * @see Component#setOutputMarkupId(boolean)
	 */
	public DragSource drag(String selector)
	{
		this.selector = selector;

		if (this.initiateSelector.equals(Transfer.UNDEFINED))
		{
			this.initiateSelector = selector;
		}

		if (this.cloneSelector.equals(Transfer.UNDEFINED))
		{
			this.cloneSelector = selector;
		}

		return this;
	}

	/**
	 * Initiate drag on elements matching the given selector.
	 * 
	 * @param selector
	 *            element selector
	 */
	public DragSource initiate(String selector)
	{
		this.initiateSelector = selector;
		return this;
	}

	/**
	 * Clone drag on elements matching the given selector.
	 * 
	 * @param selector
	 *            element selector
	 */
	public DragSource clone(String selector)
	{
		this.cloneSelector = selector;
		return this;
	}

	@Override
	public final void bind(Component component)
	{
		this.component = component;
		component.setOutputMarkupId(true);
	}

	/**
	 * Get the identifying path of this drag source.
	 * 
	 * @return path in page
	 */
	public String getPath()
	{
		return component.getPageRelativePath();
	}

	/**
	 * Get the id of this behavior.
	 * 
	 * @return id
	 */
	public int getBehaviorId()
	{
		return component.getBehaviorId(this);
	}

	@Override
	public final void renderHead(Component c, IHeaderResponse response)
	{
		super.renderHead(c, response);

		renderDragHead(response);
	}

	private void renderDragHead(IHeaderResponse response)
	{
		response.render(JavaScriptHeaderItem.forReference(Transfer.JS));

		final String id = component.getMarkupId();
		final String path = component.getPageRelativePath();
		int behavior = component.getBehaviorId(this);
		
		String initJS = String
				.format("wicketdnd.dragSource('%s','%s','%s', %s,%s,{'select':'%s','initiate':'%s','clone':'%s'});",
						id, behavior, path, new CollectionFormattable(getOperations()),
						new CollectionFormattable(getTypes()), selector, initiateSelector,
						cloneSelector);
		response.render(OnDomReadyHeaderItem.forScript(initJS));
	}

	@Override
	public void onComponentTag(Component component, ComponentTag tag)
	{
		super.onComponentTag(component, tag);

		tag.append("class", "dnd-drag-source", " ");
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

	final boolean hasOperation(Operation operation)
	{
		return getOperations().contains(operation);
	}

	final void beforeDrop(Request request, Transfer transfer) throws Reject
	{
		Component drag = getDrag(request);

		onBeforeDrop(drag, transfer);
	}

	final void afterDrop(AjaxRequestTarget target, Transfer transfer)
	{
		onAfterDrop(target, transfer);
	}

	/**
	 * Notification that a drop is about to happen - any implementation should
	 * set the data on the given transfer or reject it.
	 * 
	 * The default implementation uses the component's model object as transfer
	 * data.
	 * 
	 * @param drag
	 *            component to get data from
	 * @param transfer
	 *            the transfer
	 * @throws Reject
	 *             may reject the drop
	 * @see Transfer#setData(Object)
	 * @see Transfer#reject()
	 */
	public void onBeforeDrop(Component drag, Transfer transfer) throws Reject
	{
		transfer.setData(drag.getDefaultModelObject());
	}

	/**
	 * Notification that a drop happened of one of this source's transfer datas.
	 * 
	 * The default implementation does nothing.
	 * 
	 * @param target
	 *            initiating request target
	 * @param transfer
	 *            the transfer
	 */
	public void onAfterDrop(AjaxRequestTarget target, Transfer transfer)
	{
	}

	private Component getDrag(Request request)
	{
		String drag = request.getRequestParameters().getParameterValue("drag").toString();

		return MarkupIdVisitor.getComponent((MarkupContainer)component, drag);
	}

	/**
	 * Get the drag source of the given request.
	 * 
	 * @param request
	 *            request on which a drag happened
	 * @return drag source
	 */
	final static DragSource read(Page page, Request request)
	{
		String path = request.getRequestParameters().getParameterValue("path").toString();
		Component component = page.get(path);
		if (component == null)
		{
			throw new PageExpiredException("No drag source found " + path);
		}

		int behavior =  request.getRequestParameters().getParameterValue("behavior").toInt();
		return (DragSource)component.getBehaviorById(behavior);
	}
}
