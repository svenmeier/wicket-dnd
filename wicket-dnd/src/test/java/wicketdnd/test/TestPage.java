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
package wicketdnd.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

import wicketdnd.DragSource;
import wicketdnd.DropTarget;
import wicketdnd.Location;
import wicketdnd.Operation;
import wicketdnd.Reject;
import wicketdnd.Transfer;

public class TestPage extends WebPage
{

	public List<String> log = new ArrayList<String>();

	public WebMarkupContainer dragSource;

	public WebMarkupContainer drag;

	public WebMarkupContainer dropTarget;

	public WebMarkupContainer drop;

	public TestPage(Operation... operations)
	{

		dragSource = new WebMarkupContainer("dragSource");
		dragSource.add(new DragSource(operations)
		{
			@Override
			public void onBeforeDrop(Component drag, Transfer transfer) throws Reject
			{
				super.onBeforeDrop(drag, transfer);

				log.add("onBeforeDrop");
			}

			@Override
			public void onAfterDrop(AjaxRequestTarget target, Transfer transfer)
			{
				log.add("onAfterDrop");
			}
		});
		add(dragSource);

		drag = new WebMarkupContainer("drag");
		drag.setOutputMarkupId(true);
		dragSource.add(drag);


		dropTarget = new WebMarkupContainer("dropTarget");
		dropTarget.add(new DropTarget(operations)
		{
			@Override
			public void onDrag(AjaxRequestTarget target, Location location)
			{
				log.add("onDrag");
			}

			@Override
			public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location)
					throws Reject
			{
				log.add("onDrop");
			}
			
			@Override
			public void onRejected(AjaxRequestTarget target)
			{
				log.add("onRejected");
			}
		});
		add(dropTarget);

		drop = new WebMarkupContainer("drop");
		drop.setOutputMarkupId(true);
		dropTarget.add(drop);
	}
}