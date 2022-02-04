SikulixTest001 = "SikulixTest001.png"
img = "img.png"
find(img).highlight(1)
highlight(getAll(img), 1)

img100 = "img100.png"
img125 = Pattern(img100).resize(1.25)
img150 = Pattern(img100).resize(1.5)
img200 = Pattern(img100).resize(2)

find(img100).highlight(1)
find(img125).highlight(1)
find(img150).highlight(1)
find(img200).highlight(1)
