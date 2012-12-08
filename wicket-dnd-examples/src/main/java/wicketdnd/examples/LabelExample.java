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
package wicketdnd.examples;

import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import wicketdnd.DragSource;
import wicketdnd.DropTarget;
import wicketdnd.Location;
import wicketdnd.Operation;
import wicketdnd.Reject;
import wicketdnd.Transfer;

/**
 * @author Sven Meier
 */
public class LabelExample extends Example
{
	
	private Model<Foo> model = Model.of(new Foo("A"));
	
	public LabelExample(String id)
	{
		super(id);

		final WebMarkupContainer container = new WebMarkupContainer("container");
		container.add(new DragSource()
		{
			@Override
			public Set<Operation> getOperations()
			{
				return dragOperations();
			}

			@Override
			public String[] getTypes()
			{
				return types();
			}

			@Override
			public void onAfterDrop(AjaxRequestTarget target, Transfer transfer)
			{
				if (transfer.getOperation() == Operation.MOVE)
				{
					Foo foo = transfer.getData();
					if (foo == model.getObject()) {
						model.setObject(null);
					}
					
					target.add(container);
				}
			}
		}.drag("span"));
		container.add(new DropTarget()
		{
			@Override
			public Set<Operation> getOperations()
			{
				return dropOperations();
			}

			@Override
			public String[] getTypes()
			{
				return types();
			}

			@Override
			public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location)
					throws Reject
			{
				model.setObject(operate(transfer));

				target.add(container);
			}
		}.dropCenter(".labelContainer"));
		content.add(container);
		
		final Label label = new Label("label", model)
		{
			@Override
			public boolean isVisible()
			{
				return model.getObject() != null;
			}
		};
		label.setOutputMarkupId(true);
		container.add(label);
	}
}