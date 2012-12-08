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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
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
public class TableExample extends Example
{
	public TableExample(String id)
	{
		super(id);

		final FooDataProvider provider = new FooDataProvider();

		final DataTable<Foo, String> table = new DefaultDataTable<Foo,String>("table", columns(), provider,
				Integer.MAX_VALUE)
		{
			@Override
			protected Item<Foo> newRowItem(String id, int index, IModel<Foo> model)
			{
				Item<Foo> item = super.newRowItem(id, index, model);
				item.setOutputMarkupId(true);
				return item;
			}
		};
		table.add(new DragSource()
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

					provider.remove(foo);

					target.add(table);
				}
			}
		}.drag("tbody tr").clone("td:first"));
		table.add(new DropTarget()
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
				if (location.getComponent() == table)
				{
					provider.add(operate(transfer));
				}
				else
				{
					Foo foo = location.getModelObject();
					switch (location.getAnchor())
					{
						case TOP :
							provider.addBefore(operate(transfer), foo);
							break;
						case BOTTOM :
							provider.addAfter(operate(transfer), foo);
							break;
						default :
							transfer.reject();
					}
				}

				target.add(table);
			}
		}.dropTopAndBottom("tbody tr").dropCenter("table"));
		content.add(table);
	}

	private List<IColumn<Foo,String>> columns()
	{
		List<IColumn<Foo,String>> columns = new ArrayList<IColumn<Foo,String>>();
		columns.add( new PropertyColumn<Foo,String>(Model.of("Name"), "name"));
		columns.add( new PropertyColumn<Foo,String>(Model.of("Name"), "name"));
		return columns;
	}
}