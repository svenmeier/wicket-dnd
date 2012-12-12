wicket-dnd
==========

A generic Drag&Drop framework for [http://wicket.apache.org Wicket]:

- operate on any markup element via selectors
- drag and drop between any Wicket components
- vertical, horizontal and hierarchical structured markup (i.e. [http://code.google.com/p/wicket-tree/ trees])
- drag initiators (a.k.a. handles)
- common desktop metaphors with 'MOVE', 'COPY' and 'LINK' [http://code.google.com/p/wicket-dnd/source/browse/trunk/wicket-dnd/src/main/java/wicketdnd/Operation.java operations]
- transfer types
- themeable
- works in Firefox, Safari, Chrome, Opera

See our live examples on [http://wicket-dnd.appspot.com](http://wicket-dnd.appspot.com) (beware - very slow!).

Themes
------

You have to add a theme (build-in or your own) to your component you want to enable for DnD, e.g.

    add(new WindowsTheme());

Drag source
-----------

Enable a component as a source of drags:

    container.add(new DragSource(Operation.MOVE) {
      public void onAfterDrop(AjaxRequestTarget target, Transfer transfer) {
        // remove transfer data
      }
    }.drag("tr"));

In this example only a MOVE operation is allowed. Drags are initiated on `<tr> tags.

Drop target
-----------

Enable a component as a target for drops:

    container.add(new DropTarget(Operation.MOVE, Operation.COPY) {
      public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location) {
        // add transfer data
      }
    }.dropCenter("tr"));

In this example MOVE and COPY operations are allowed. Drops are performed on center of `<tr>` tags,
the location holds a reference to the actual component and the anchor the transfer was dropped on.
