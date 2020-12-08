# ImageFinder
Simple Java-API that allows for image-finding with extra options such as accuracy, pixel accuracy, scaleX and scaleY.


usage:
```java
double scaleX = 1;                  /* 1 = 100%, 0 = 0% */
double scaleY = 1;                  /* 1 = 100%, 0 = 0% */
double minPixelSimilarity = 0.9;    /* 1 = 100%, 0 = 0% */
double minColorSimilarity = 0.9;    /* 1 = 100%, 0 = 0% */
ImageFinderChild c = new ImageFinder(/*path to png or BufferedImage to find*/"",
        minColorSimilarity,minPixelSimilarity,scaleX,scaleY)
        .find(/*optional: path to png or BufferedImage to find the image on. With no parameters, uses a screenshot*/);
String time = c.getTime();
Point midPoint = c.getMidPoint();
Point startingPoint = c.getImageStartingPoint();
        ```
