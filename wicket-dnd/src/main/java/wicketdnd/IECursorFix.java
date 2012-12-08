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
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Fix for cursors in IE. <br>
 * IE resolves relative URLs in CSS cursor styles against the page's location
 * instead of the CSS's location (note: this failure affects cursor images
 * only). This javascript makes these URLs absolute by prepending the containing
 * CSS's location.
 * 
 * @author Sven Meier
 */
public class IECursorFix extends Behavior
{

	private static final long serialVersionUID = 1L;

	public static final ResourceReference JS = new JavaScriptResourceReference(IECursorFix.class,
			"iecursor.js");

	@Override
	public void renderHead(Component component, IHeaderResponse response)
	{
		WebClientInfo info = new WebClientInfo(RequestCycle.get());
		if (info.getProperties().isBrowserInternetExplorer())
		{
			response.render(JavaScriptHeaderItem.forReference(JS));

			String initJS = "IECursor.fix();";
			response.render(OnDomReadyHeaderItem.forScript(initJS));
		}
	}
}
