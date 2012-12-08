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
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;

import wicketdnd.Operation;
import wicketdnd.Transfer;

/**
 * @author Sven Meier
 */
public abstract class Example extends Panel
{

	private List<Operation> operations;

	private List<Operation> dragOperations = new ArrayList<Operation>();

	private List<Operation> dropOperations = new ArrayList<Operation>();

	private String[] types = new String[] { Transfer.ANY };

	protected WebMarkupContainer content;

	public Example(String id)
	{
		super(id);

		content = new WebMarkupContainer("content");
		content.setOutputMarkupId(true);
		add(content);

		operations = new ArrayList<Operation>();
		operations.addAll(EnumSet.allOf(Operation.class));

		dragOperations.addAll(operations);
		dropOperations.addAll(operations);

		add(new Label("title", new AbstractReadOnlyModel<String>()
		{
			@Override
			public String getObject()
			{
				String name = Example.this.getClass().getSimpleName();

				return name.substring(0, name.indexOf("Example"));
			}
		}));

		final WebMarkupContainer controls = new WebMarkupContainer("controls",
				new CompoundPropertyModel<Example>(this));
		add(controls);

		controls.add(new CheckBoxMultipleChoice<Operation>("dragOperations", operations).setSuffix(
				"").add(new AjaxFormChoiceComponentUpdatingBehavior()
		{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(content);
			}
		}));

		controls.add(new CheckBoxMultipleChoice<Operation>("dropOperations", operations).setSuffix(
				"").add(new AjaxFormChoiceComponentUpdatingBehavior()
		{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(content);
			}
		}));

		controls.add(new TextField<String[]>("types", String[].class)
		{
			@Override
			@SuppressWarnings("unchecked")
			public <C> IConverter<C> getConverter(Class<C> type)
			{
				return (IConverter<C>)new StringArrayConverter();
			}
		}.setConvertEmptyInputStringToNull(false).add(new OnChangeAjaxBehavior()
		{
			@Override
			protected void onUpdate(AjaxRequestTarget target)
			{
				target.add(content);
			}
		}));
	}

	protected Set<Operation> dragOperations()
	{
		if (dragOperations.isEmpty())
		{
			return EnumSet.noneOf(Operation.class);
		}
		else
		{
			return EnumSet.copyOf(dragOperations);
		}
	}

	protected Set<Operation> dropOperations()
	{
		if (dropOperations.isEmpty())
		{
			return EnumSet.noneOf(Operation.class);
		}
		else
		{
			return EnumSet.copyOf(dropOperations);
		}
	}

	public String[] types()
	{
		return types;
	}

	public void setTypes(String[] types)
	{
		this.types = types;
	}

	protected Foo operate(Transfer transfer)
	{
		Foo foo = transfer.getData();
		switch (transfer.getOperation())
		{
			case MOVE :
			case COPY :
				return foo.copy();
			case LINK :
				return foo.link();
			default :
				throw new IllegalArgumentException();
		}
	}

	private class StringArrayConverter implements IConverter<String[]>
	{
		public String[] convertToObject(String value, Locale locale)
		{
			String[] strings = Strings.split(value, ',');
			if (strings.length == 0)
			{
				strings = new String[] { "" };
			}
			return strings;
		}

		public String convertToString(String[] value, Locale locale)
		{
			return Strings.join(",", (String[])value);
		}
	}
}