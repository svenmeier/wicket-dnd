wicket-dnd
==========

A generic Drag&Drop framework for [Wicket](http://wicket.apache.org):

(see [http://code.google.com/p/wicket-dnd](http://code.google.com/p/wicket-dnd) for Wicket 1.4 and 1.5)

- operate on any markup element via selectors
- drag and drop between any Wicket components
- vertical, horizontal and hierarchical structured markup
- drag initiators (a.k.a. handles)
- common desktop metaphors with `MOVE`, `COPY` and `LINK` operations
- transfer types
- themeable
- works in Firefox, Safari, Chrome, Opera

See our live examples on [http://wicket-dnd.appspot.com](http://wicket-dnd.appspot.com) (beware - very slow!).

Themes
------

You have to add a theme (build-in or your own) to your component you want to enable for DnD, e.g.

    container.add(new WindowsTheme());

Drag source
-----------

Enable a component as a source of drags:

    container.add(new DragSource(Operation.MOVE) {
      public void onAfterDrop(AjaxRequestTarget target, Transfer transfer) {
        // remove transfer data
      }
    }.drag("tr"));

In this example only a MOVE operation is allowed. Drags are initiated on `<tr>` tags.

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

Maven dependency
----------------

Releases are available on [Maven central](http://repo1.maven.org/maven2/com/github/svenmeier/wicket-dnd)
and [OSS Sonatype](https://oss.sonatype.org/content/repositories/releases/com/github/svenmeier/wicket-dnd).

    <dependency>
      <groupId>com.github.svenmeier.wicket-dnd</groupId>
      <artifactId>wicket-dnd</artifactId>
      <version>0.6.0</version>
    </dependency>

For snapshot releases you have to use the [OSS Sonatype Snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/svenmeier/wicket-dnd/):

    <dependency>
      <groupId>com.github.svenmeier.wicket-dnd</groupId>
      <artifactId>wicket-dnd</artifactId>
      <version>0.6.1-SNAPSHOT</version>
    </dependency>

    <repository>
      <id>sonatype-snapshots</id>
      <url>https://oss.sonatype.org/content/repositories/snapshots</url>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>		
    </repository>
