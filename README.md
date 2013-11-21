Builder plugin for Intellij
================
An Intellij plugin to generate a nested static Builder for a Class.

Why another Builder plugin?
---------------------------
There were several Builder plugins already out there, but none that generated a Builder that conformed to the pattern
laid out in [Effective Java](https://www.google.com/search?q=effective+java), which we use extensively.  In addition,
the [Features](/README.md#features) outlines other goodies.

Install
-------
* Download the .jar file from https://git.squareup.com/rhall/builder-plugin/releases
* From Intellij: Preferences -> Plugins -> Install plugin from disk...
* Restart Intellij

Update
------
* Follow the Install instructions for the latest release .jar file.
* Intellij seems to be buggy and will report the plugin can be upgraded.  Just ignore.  The version should be correct on the right pane.
* If something goes wrong, you can always uninstall the plugin and install the latest version.

Use
-------
* From a Class, right click -> Generate... -> Builder...
* Remove any fields you don't want and select which fields are nullable

Features
--------
* Creates a static nested Builder that conforms to the pattern in Effective Java.
* Select what fields you want to include in the Builder.
* A <code>Preconditions.checkNotNull</code> will be added to the constructor for non-null fields.
* If <code>javax.persistence</code> annotations exist on fields, nullable will be inferred for the dialog.
* Can optionally generate getters, which will return a Guava <code>Optional</code> if the field is nullable.
