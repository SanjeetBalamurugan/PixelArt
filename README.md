# PixelArt Mod Usage

## Commands

### Render
```
/pixelart render <url>
/pixelart render <maxheight> <url>
/pixelart render <orientation> <url>
/pixelart render <maxheight> <orientation> <url>
```

### Erase
```
/pixelart erase <url>
/pixelart erase <maxheight> <url>
/pixelart erase <orientation> <url>
/pixelart erase <maxheight> <orientation> <url>
```

### Undo
```
/pixelart undo
```

## Parameters

- `<url>` - Direct URL to an image (PNG, JPG, etc.)
- `<maxheight>` - Maximum height in blocks (1-100)
- `<orientation>` - Either `vertical` (default) or `horizontal`

## Examples

```
/pixelart render https://example.com/image.png
/pixelart render 50 https://example.com/image.png
/pixelart render horizontal https://example.com/image.png
/pixelart render 50 vertical https://example.com/image.png
/pixelart erase https://example.com/image.png
/pixelart undo
```
