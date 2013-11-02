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

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.util.tester.WicketTester;

import wicketdnd.DragSource;
import wicketdnd.DropTarget;
import wicketdnd.Location;
import wicketdnd.Operation;

/**
 * A test for DnD operations.
 * 
 * @author svenmeier
 */
public class DnDTester
{

	private WicketTester tester;

	public DnDTester(WicketTester tester)
	{
		this.tester = tester;
	}

	/**
	 * Execute a drag over a location in the given dropTarget.
	 * 
	 * @param dropTarget
	 *            target of drops
	 * @param location
	 *            drag over location
	 */
	public void executeDrag(Component dropTarget, Location location)
	{
		executeDrag(getBehavior(dropTarget, DropTarget.class), location);
	}

	/**
	 * Execute a drag over a location in the given dropTarget.
	 * 
	 * @param dropTarget
	 *            target of drops
	 * @param location
	 *            drag over location
	 */
	public void executeDrag(DropTarget dropTarget, Location location)
	{
		MockHttpServletRequest request = tester.getRequest();

		request.setParameter("phase", "drag");

		request.setParameter("component", location.getComponent().getMarkupId());
		request.setParameter("anchor", location.getAnchor().name());

		tester.executeBehavior(dropTarget);
	}

	/**
	 * Execute a drop on a location in the given dropTarget
	 * 
	 * @param dropTarget
	 *            target of drops
	 * @param location
	 *            drop location
	 * @param operation
	 *            DnD operation
	 * @param dragSource
	 *            source of drags
	 * @param drag
	 *            dragged component
	 */
	public void executeDrop(Component dropTarget, Location location, Operation operation,
			Component dragSource, Component drag)
	{
		executeDrop(getBehavior(dropTarget, DropTarget.class), location, operation,
				getBehavior(dragSource, DragSource.class), drag);
	}

	/**
	 * Execute a drop on a location in the given dropTarget
	 * 
	 * @param dropTarget
	 *            target of drops
	 * @param location
	 *            drop location
	 * @param operation
	 *            DnD operation
	 * @param dragSource
	 *            source of drags
	 * @param drag
	 *            dragged component
	 */
	public void executeDrop(DropTarget dropTarget, Location location, Operation operation,
			DragSource dragSource, Component drag)
	{
		MockHttpServletRequest request = tester.getRequest();

		request.setParameter("phase", "drop");

		request.setParameter("component", location.getComponent().getMarkupId());
		request.setParameter("anchor", location.getAnchor().name());

		request.setParameter("operation", operation.name());

		request.setParameter("path", dragSource.getPath());
		request.setParameter("behavior", "" + dragSource.getBehaviorId());
		request.setParameter("drag", drag.getMarkupId());

		tester.executeBehavior(dropTarget);
	}

	private <T extends Behavior> T getBehavior(Component component, Class<T> clazz)
	{
		List<T> behaviors = component.getBehaviors(clazz);
		if (behaviors.size() == 0)
		{
			throw new WicketRuntimeException("no behavior of type " + clazz.getName());
		}
		else if (behaviors.size() > 1)
		{
			throw new WicketRuntimeException("multiple behaviors of type " + clazz.getName());
		}

		return behaviors.get(0);
	}
}
