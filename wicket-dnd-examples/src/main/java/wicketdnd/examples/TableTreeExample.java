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
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree.State;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultTableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
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
public class TableTreeExample extends Example
{
	public TableTreeExample(String id)
	{
		super(id);
		
		final FooTreeProvider provider = new FooTreeProvider();
		
		final TableTree<Foo,String> tabletree = new DefaultTableTree<Foo,String>("tabletree", columns(),
				provider, Integer.MAX_VALUE);
		// reuse items or drop following expansion will fail due to new
		// markup ids
		tabletree.setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
		tabletree.add(new DragSource()
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
					
					foo.remove();

					target.add(tabletree);
				}
			}
		}.drag("tr").initiate("span.tree-content").clone("span.tree-content"));
		tabletree.add(new DropTarget()
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
			public void onDrag(AjaxRequestTarget target, Location location)
			{
				Foo foo = location.getModelObject();
				if (tabletree.getState(foo) == State.COLLAPSED) {
					tabletree.expand(foo);
				}
			}

			@Override
			public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location)
					throws Reject
			{
					Foo foo = location.getModelObject();
					if (foo.isAncestor(transfer.getData())) {
						transfer.reject();
					}
					
					switch (location.getAnchor())
					{
						case CENTER :
							if (foo == transfer.getData()) {
								transfer.reject();
							}
							provider.add(operate(transfer), foo);
							tabletree.expand(foo);
							break;
						case TOP :
							provider.addBefore(operate(transfer), foo);
							break;
						case BOTTOM :
							provider.addAfter(operate(transfer), foo);
							break;
						default :
							transfer.reject();
					}

					target.add(tabletree);
			}
		}.dropCenter("tbody tr"));
		content.add(tabletree);
	}
	
	private List<IColumn<Foo,String>> columns()
	{
		List<IColumn<Foo,String>> columns = new ArrayList<IColumn<Foo,String>>();
		
		columns.add(new TreeColumn<Foo,String>(Model.of("Name")));
		columns.add(new PropertyColumn<Foo,String>(Model.of("Name"), "name"));
		
		return columns;
	}
}