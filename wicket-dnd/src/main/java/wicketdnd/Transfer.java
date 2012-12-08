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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * A transfer from a {@link DragSource} to a {@link DropTarget}, only allowed when:
 * <ul>
 *   <li>source and target share a common transfer {@link Operation}</li>
 *   <li>source and target share a common transfer type ({@link #getType()})</li>
 *   <li>{@link DragSource#onBeforeDrop(Component, Transfer)} doesn't reject the drop</li>
 *   <li>{@link DropTarget#onDrop(AjaxRequestTarget, Transfer, Location)} doesn't reject the drop</li>
 * </ul>
 * 
 * @author Sven Meier
 */
public class Transfer
{

	public static final ResourceReference JS = new JavaScriptResourceReference(Transfer.class,
			"wicket-dnd.js");

	/**
	 * Undefined CSS selector.
	 */
	public static final String UNDEFINED = "undefined";

	/**
	 * Transfer type indicating any type supported.
	 * 
	 * @see DragSource#getTypes()
	 * @see DropTarget#getTypes()
	 */
	public static final String ANY = "";

	private String type;

	private Operation operation;

	private Object data;

	Transfer(String type, Operation operation)
	{
		this.type = type;
		this.operation = operation;
	}

	/**
	 * Get type of transfer.
	 * 
	 * @see DragSource#getTypes()
	 * @see DropTarget#getTypes()
	 * 
	 * @return type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Get the operation of this transfer.
	 * 
	 * @see DragSource#getOperations()
	 * @see DropTarget#getOperations()
	 */
	public Operation getOperation()
	{
		return operation;
	}

	/**
	 * Set the data of this transfer.
	 * 
	 * @see DragSource#onBeforeDrop(Component, Transfer)
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 * Get the data of this transfer.
	 * 
	 * @see DropTarget#onDrop(AjaxRequestTarget, Transfer, Location)
	 * @see DragSource#onAfterDrop(AjaxRequestTarget, Transfer)
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData()
	{
		return (T)this.data;
	}

	/**
	 * Reject this transfer.
	 */
	public void reject() throws Reject
	{
		throw new Reject();
	}
}