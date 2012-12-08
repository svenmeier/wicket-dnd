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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree.State;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.model.IModel;

import wicketdnd.DragSource;
import wicketdnd.DropTarget;
import wicketdnd.Location;
import wicketdnd.Operation;
import wicketdnd.Reject;
import wicketdnd.Transfer;

/**
 * @author Sven Meier
 */
public class TreeExample extends Example
{
	public TreeExample(String id)
	{
		super(id);

		final FooTreeProvider provider = new FooTreeProvider();
		final NestedTree<Foo> tree = new DefaultNestedTree<Foo>("tree", provider)
		{
			@Override
			protected Component newContentComponent(String arg0, IModel<Foo> arg1)
			{
				Component component = super.newContentComponent(arg0, arg1);
				component.setOutputMarkupId(true);
				return component;
			}
		};
		tree.add(new DragSource()
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

					target.add(tree);
				}
			}
		}.drag("span.tree-content"));
		tree.add(new DropTarget()
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
				if (location.getComponent() != tree)
				{
					Foo foo = location.getModelObject();
					if (tree.getState(foo) == State.COLLAPSED) {
						tree.expand(foo);
					}
				}
			}

			@Override
			public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location)
					throws Reject
			{
				if (location.getComponent() == tree)
				{
					provider.add(operate(transfer));
				}
				else
				{
					Foo foo = location.getModelObject();
					if (foo.isAncestor(transfer.getData()))
					{
						transfer.reject();
					}

					switch (location.getAnchor())
					{
						case CENTER :
							if (foo == transfer.getData())
							{
								transfer.reject();
							}
							provider.add(operate(transfer), foo);

							tree.expand(foo);
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
				}
				target.add(tree);
			}
		}.dropCenter("span.tree-content, div.treeContainer").dropTopAndBottom("div.tree-branch"));
		tree.setOutputMarkupId(true);
		content.add(tree);
	}
}