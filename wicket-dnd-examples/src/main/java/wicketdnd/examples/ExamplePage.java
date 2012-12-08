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

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;

import wicketdnd.IEBackgroundImageCacheFix;
import wicketdnd.IECursorFix;
import wicketdnd.theme.HumanTheme;
import wicketdnd.theme.WebTheme;
import wicketdnd.theme.WindowsTheme;

/**
 * @author Sven Meier
 */
public class ExamplePage extends WebPage
{

	private static final long serialVersionUID = 1L;

	private List<Behavior> themes;

	private Behavior theme;

	public ExamplePage()
	{
		add(new IECursorFix());
		add(new IEBackgroundImageCacheFix());

		// for a static theme just add a theme like the following:
		// form.add(new WebTheme()));
		
		// dynamic theme selection
		Form<Void> form = new Form<Void>("form");
		form.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onComponentTag(Component component, ComponentTag tag)
			{
				theme.onComponentTag(component, tag);
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response)
			{
				theme.renderHead(component, response);
			}
		});
		add(form);

		form.add(new DropDownChoice<Behavior>("theme",
				new PropertyModel<Behavior>(this, "theme"), initThemes(),
				new ChoiceRenderer<Behavior>("class.simpleName"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}
		});

		RepeatingView examples = new RepeatingView("examples");
		examples.add(new LabelExample(examples.newChildId()));
		examples.add(new ListsExample(examples.newChildId()));
		examples.add(new TableExample(examples.newChildId()));
		examples.add(new TreeExample(examples.newChildId()));
		examples.add(new TableTreeExample(examples.newChildId()));
		form.add(examples);
	}

	private List<Behavior> initThemes()
	{
		themes = new ArrayList<Behavior>();

		themes.add(new WindowsTheme());
		themes.add(new HumanTheme());
		themes.add(new WebTheme());

		theme = themes.get(0);

		return themes;
	}
}