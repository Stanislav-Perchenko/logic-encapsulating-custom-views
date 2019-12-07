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


### UI sample ###

![Empty editors](/docs/EditablePropertyView-1.png "Empty editors")

![Filled editors](/docs/EditablePropertyView-2.png "Filled editors")

Here you can see the [**video**][1] of using this widget.


Collecting Goods
-------------

The agenda of this demo is that a *buyer* collects some goods of different categories () and pays for them.
Goods are collected by weights. The set of good categories and related prices are supposed to be loaded
from a backend, but hard-coded in this demo. The *seller* must put its signature at the end.

The form of goods collecting is organized as the several sliding screen. The 1-st one displays currently-collected
set of goods, total amount to be payed and provided seller's signature. The others are used for making choices of
collecting goods, their weights and editing signature.

There is the full [**video**][2] of using this demo.

The 1-st page with some goods selected and *seller* data provided
![First page](/docs/collect-goods-1.png "First page")


Page 2 - goods selector
![Page 2 - goods selector](/docs/collect-goods-2.png "Page 2 - goods selector")

### Custom View which encapsulate layouts and business-logic ###

- **CollectedGoodItemView** - this one is used to show selected goods on the 1-st page. It is instantiated from code and added to the container layout dynamically.
- **SelectGoodItemView** - this View completely implements page 2 of this demo: single-choice selection list for good type and sliders for the selected weight of a good.




[1]: /docs/editable-property-view.mp4
[2]: /docs/collecting-goods.mp4
