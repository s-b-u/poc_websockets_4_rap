PoC Websockets for Eclipse RAP
=================================================

This project is just a proof of concept and therefore as dirty as it is. It's here only for the use, to get an idea how much effort it will take to bring RAP and JSR356 together.
The main driver for the idea is to decrease network latency to make RAP applications behave smooth even on a network with a perceptible round trip time ( proxy, firewall, ssl-handshake and so on ).  

Git Repository Structure
------------------------



| directory   | content                                                     |
|-------------|-------------------------------------------------------------|
| `cnf/`  | bnd workspace                                         |
| `org.eclipse.jetty.osgi.httpservice.websocket/`    | replacement for `org.eclipse.jetty.osgi.httpservice` to enable jsr356 websockets                                       
| `org.eclipse.rap.e4.ws.demo/` | copied from [RAP E4 Demo][2] because of [Bug 547896][3]|
| `org.eclipse.rap.rwt.osgi.websocket/`   | main project to wrap the client-server communication within a websocket|



Run the RAP Demo Applications
--------------------

Run the following command from the root of the Git repository (at least JDK-8 required):

    gradlew build run.rap-controls-demo

or

    gradlew build run.rap-examples-demo

After the application is built and started, point your browser to http://localhost:9090.

You can also start the RAP E4 Demo application by: (at least JDK-9 required):

    gradlew build run.rap-e4-demo

which can be found at http://localhost:9090/poc/hello

You should now be able to inspect the traffic with the tool of your choice.
As an example, how it should look like, here is a screen shoot of [Chrome DevTools][5]  

![Chrome Debug View](chrome-dbg.png)


License
-------

[Eclipse Public License (EPL) v2.0][6]


Known Limitations / Todos
-------
- FileUpload not working - needs to be revisited
- startup of [RAP Workbench Demo][8] fails due to [Bug 547895][7]


[1]: http://eclipse.org/rap
[2]: https://github.com/eclipse/rap/tree/master/examples/org.eclipse.rap.e4.demo
[3]: https://bugs.eclipse.org/bugs/show_bug.cgi?id=547896
[4]: http://localhost:9090/poc/hello
[5]: https://developers.google.com/web/tools/chrome-devtools/
[6]: https://www.eclipse.org/legal/epl-2.0/
[7]: https://bugs.eclipse.org/bugs/show_bug.cgi?id=547895
[8]: https://www.eclipse.org/rap/demos/
