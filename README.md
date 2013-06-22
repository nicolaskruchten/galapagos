Galapagos was created as part of my [undergraduate thesis](http://nicolas.kruchten.com/thesis.pdf) at the University of Toronto.

Detailed documentation is available in the 'galapagosmanual.pdf' file as well as in the thesis linked above and the [corresponding blog post](http://nicolas.kruchten.com/content/2013/06/galapagos/)

To compile, just execute `ant`

To run a demo, you'll need to open up four separate shells and execute the following commands, one per shell, in the order given:

<pre>
$ java -cp "ext/*:lib/*" ca.utoronto.civ.its.lightgrid.dispatcher.Dispatcher
$ java -cp "ext/*:lib/*" ca.utoronto.civ.its.lightgrid.resource.Resource
$ java -cp "ext/*:lib/*" ca.utoronto.civ.its.galapagos.container.Container
$ java -cp "ext/*:lib/*" ca.utoronto.civ.its.galapagos.controller.gui.GUIController
</pre>

The last command will launch a UI which will let you select a configuration file to run, and a sample config file is provided called 'config.xml'.