# Service description

This service uses Java Swing to build some simple UI components. This
components can be used to e.g. retrieve a password via a dialog from the user.

*Attention*: This service should be used as a fallback. Normally services are
called from a Sweetp client. This can be everything, e.g. a CLI tool, a Website
... This client should get data from the user and pass it as parameters to
the service. If this isn't possible or usable, use this service to get data
from the user.

# dev

Use git submodule to fetch the content of the lib directory:

    git submodule init
    git submodule update

This project uses [gradle](http://gradle.org) as build system. Make sure
you installed version 1.0-milestone9. All other stuff needed comes with
gradle.

After adding some source code and changed the basename you can run:

* "gradle test" to test your code with junit
* "gradle check" to check if your code meets the codenarc standards
* "gradle idea" to generate project files for Intellij IDEA IDE
* "gradle eclipse" to generate project files for Eclipse IDE
* "gradle jar" to zip your service

the last command generates a jar file in service/build/libs which
you can then move into your sweetp services dir:
'SWEETPROOTDIR/server/services'.
Don't forgett to add a new entry in your services.json and restart the server.

More Information on [sweet productivity](http://sweet-productivity.com).
