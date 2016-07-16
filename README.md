# Apiro

A lightweight API Routing library for HTTP servlets without framework or
annotations.

## Objects

__Routers__ route HttpServletRequest and HttpServletResponse pairs to
__Endpoints__. The routing is guided by matching regular expressions.

__Endpoints__ are interfaces that the user application implements. The
interface consists of a `handle()` method. The method is provided with the
request, response, and matched matcher.

    public void handle(HttpServletRequest req, HttpServletResponse res,
        Matcher matcher);

## Binding

Suppose we have router `R` and endpoint `E`, and that we wanted requests with
path matching regex pattern `P` to route to `E`. Here is how we would bind them.

    R.on(P, E);

For example, binding a `ListDocsEndpoint` to a `GET` router on the `/doc` path.

    GET.on("/doc/?", new ListDocsEndpoint());

> The `/?` suffix allows the binding to work for both `/doc` and `/doc/`.

## Path Parameters

Use regular expressions to capture path parameters.

    Router GET = new Router();
    GET.on("/doc/([^/]+)/?", new GetDocEndpoint());

In the endpoint, the document ID can be obtained from the matcher.

    String docId = matcher.group(1);

## Named Path Parameters

Path parameters can be named; using named groups.

    Router GET = new Router();
    GET.on("/doc/(?<id>[^/]+)/page/(?<pg>[0-9]+)/?", new GetPageEndpoint());

In the corresponding endpoint we can use the names to access parameter values.

    String docId = matcher.group("id");
    String pgno = matcher.group("pg");

See [java.util.regex.Pattern](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) for more regex trickery!

## Dispatching

As simple as calling dispatch.

    GET.dispatch(req, res);

Well not quite. You probably should check if requests got handled by an
Endpoint and respond accordingly. For example returning 404.

    if (!GET.dispatch(req, res)) {
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

## Where do we specify HTTP method?

We don't. HttpServlet already does that for us. We only want to solve the
mapping of path (Path Info) to endpoint. To work with more than one HTTP method
type, have a router for each required HTTP method and then have each
`do<MethodType>` method dispatch using the correct router. So in `doGet` we
might invoke...

    GET.dispatch(req, res);

... and in `doPost` we would invoke...

    POST.dispatch(req, res);

Binding remains very readable.

    POST.on("/doc/?", new CreateDoc());
    GET.on("/doc/?", new ListDocs());
    GET.on("/doc/"+DOCID+"/?", new GetDoc());

## Query Parameters

A QueryString class is provided to aid in parsing query strings.
