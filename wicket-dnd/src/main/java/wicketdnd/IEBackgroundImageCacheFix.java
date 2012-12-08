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
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * Fix for caching of background images in IE.
 * <br>
 * IE 6 fails to cache images if they are used as CSS backgrounds. This
 * javascript enables caching by executing an IE proprietary command.
 * 
 * @author Sven Meier
 */
public class IEBackgroundImageCacheFix extends Behavior
{

	private static final long serialVersionUID = 1L;

	@Override
	public void renderHead(Component component, IHeaderResponse response)
	{
		WebClientInfo info = new WebClientInfo(RequestCycle.get());
		if (info.getProperties().isBrowserInternetExplorer())
		{
			String initJS = "try { document.execCommand('BackgroundImageCache', false, true); } catch (e) {};";
			response.render(OnDomReadyHeaderItem.forScript(initJS));
		}
	}
}
