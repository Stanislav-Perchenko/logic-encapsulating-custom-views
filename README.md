This is just a demonstration project for custom Custom Views encapsulating complex layout and related
business-logic. They are intended for offloading Activities and their layouts of boilerplate code.
This custom Views may be used as library artifacts.

EditablePropertyView
-------------

This custom View encapsulates EditText along with text hint, related icon ans optional marks for displaying whether this field is mandatory and has valid data.
It also encapsulated logic for entered text validation, displaying necessary marks and highlighting
all necessary elements when this editor is in focus.


Usage demonstration activity - **EditablePropertyDemoActivity.java**, module - **app**

View implementation - **EditablePropertyView.java**, module - **widgets**

### Custom properties for the View ###
- **isMandatory** - defines if this editor is mandatory to be filled-in. Appropriate parks are shown
on the right.
- **propertyName** - a hint to be displayed for this editor.
- **propertyIcon** - optional icon to be displayed on the left of this editor
- **validMarkIcon** - optional mark to be displayed if an entered data is match with the validation regex
- **mandatoryMarkColor** - this can override color of the mark which is displayed if the field is mandatory and no valid data is entered
- **valueTemplateRegex** - a regular expression for validating entered data
- **actionDrawable** - an action button may be displayed on the right of this View if valid data is entered and this property is provided

### Usage in layout ###

![Layout demo](/docs/EditablePropertyView-layout-usage.png "Layout demo")

![Left icon XML selector demo](/docs/ic_login_email_selector.png "Left icon XML selector demo")



To see what else you can do with Markdown (including **tables**, **images**, **numbered lists**, and more) take a look at the [Cheatsheet][1]. And then try it out by typing in this box!

[1]: https://github.com/adam-p/markdown-here/wiki/Markdown-Here-Cheatsheet
