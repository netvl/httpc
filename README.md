httpc - simple http client
==========================

`httpc` is very simple HTTP client (HTTPS will be supported in future) with Swing UI. It is able to assemble and send
HTTP requests for you.

Surprisingly, but I couldn't manage to find graphical HTTP client which is nothing more than HTTP client, so I
decided to write one. Because `httpc` runs over JVM, it is also cross-platform.

The closest alternative to `httpc` that I was able to find is browser plugins, like [RESTClient](http://restclient.net)
for Firefox. However, they were somewhat inconvenient in some ways. For example, I wasn't able to send HTTPS request
with RESTClient to self-signed HTTPS server, so I had to drop back to something like `SoapUI`, which clearly is
overkill.

The project is in highly alpha state, nothing is usable right now.