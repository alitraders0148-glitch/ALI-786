#!/usr/bin/env python3
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import os, math, sys

OUT = os.environ.get("ASSET_OUT", "app/src/main/res/drawable-nodpi")
os.makedirs(OUT, exist_ok=True)

SOLID = os.environ.get("FA_SOLID", "/tmp/fa-solid-900.ttf")
BRANDS = os.environ.get("FA_BRANDS", "/tmp/fa-brands-400.ttf")

try:
    F_SOLID = ImageFont.truetype(SOLID, 225)
    F_BRAND = ImageFont.truetype(BRANDS, 220)
except Exception as e:
    print("Font Awesome load failed:", e)
    sys.exit(2)

def font(path, size):
    try: return ImageFont.truetype(path, size)
    except: return ImageFont.load_default()

SANS_B = font("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 52)
SANS = font("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 40)

def rounded_gradient(c1, c2, size=512, radius=118):
    scale = 2
    S=size*scale
    shadow = Image.new("RGBA",(S,S),(0,0,0,0))
    sd=ImageDraw.Draw(shadow)
    sd.rounded_rectangle((46*scale,52*scale,(size-34)*scale,(size-28)*scale),radius*scale,fill=(0,0,0,75))
    shadow=shadow.filter(ImageFilter.GaussianBlur(22*scale))
    bg=Image.new("RGBA",(S,S),(0,0,0,0))
    pix=bg.load()
    def rgb(h): return tuple(int(h[i:i+2],16) for i in (1,3,5))
    a=rgb(c1); b=rgb(c2)
    for y in range(S):
        t=y/(S-1)
        col=tuple(int(a[i]*(1-t)+b[i]*t) for i in range(3))+(255,)
        for x in range(S): pix[x,y]=col
    mask=Image.new("L",(S,S),0)
    md=ImageDraw.Draw(mask)
    md.rounded_rectangle((30*scale,26*scale,(size-30)*scale,(size-34)*scale),radius*scale,fill=255)
    bg.putalpha(mask)
    # subtle matte highlight
    hi=Image.new("RGBA",(S,S),(0,0,0,0))
    hd=ImageDraw.Draw(hi)
    hd.rounded_rectangle((43*scale,38*scale,(size-43)*scale,225*scale),100*scale,fill=(255,255,255,30))
    hi=hi.filter(ImageFilter.GaussianBlur(22*scale))
    out=Image.alpha_composite(shadow,bg)
    out=Image.alpha_composite(out,hi)
    return out.resize((size,size),Image.Resampling.LANCZOS)

def center_text(im, txt, fnt, color=(248,247,242,255), dy=0, shadow=True):
    d=ImageDraw.Draw(im)
    bb=d.textbbox((0,0),txt,font=fnt)
    w,h=bb[2]-bb[0],bb[3]-bb[1]
    x=(im.width-w)//2
    y=(im.height-h)//2-bb[1]+dy
    if shadow:
        d.text((x+7,y+10),txt,font=fnt,fill=(0,0,0,65))
    d.text((x,y),txt,font=fnt,fill=color)

def icon_font(name, code, bg1, bg2, brand=False, fg=(248,247,242,255)):
    im=rounded_gradient(bg1,bg2)
    center_text(im, chr(int(code,16)), F_BRAND if brand else F_SOLID, fg, dy=-8)
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def chrome(name):
    im=rounded_gradient("#F6F1F1","#E9E4E4"); d=ImageDraw.Draw(im)
    box=(135,135,377,377)
    d.pieslice(box,210,330,fill="#34A853"); d.pieslice(box,330,90,fill="#EA4335"); d.pieslice(box,90,210,fill="#FBBC05")
    d.ellipse((195,195,317,317),fill="#4285F4"); d.ellipse((220,220,292,292),fill="#F8F7F3")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def flower(name):
    im=rounded_gradient("#FAF7F5","#E7E4E5"); d=ImageDraw.Draw(im)
    colors=["#F44336","#FF9800","#FFEB3B","#8BC34A","#00BCD4","#2196F3","#673AB7","#E91E63"]
    cx=256;cy=256
    for i,c in enumerate(colors):
        a=math.radians(i*45-90); x=cx+72*math.cos(a); y=cy+72*math.sin(a)
        d.ellipse((x-52,y-70,x+52,y+70),fill=c)
    d.ellipse((236,236,276,276),fill="#F7F3EF")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def playstore(name):
    im=rounded_gradient("#F8F5F2","#ECE8E7"); d=ImageDraw.Draw(im)
    pts=[(176,145),(176,367),(372,256)]
    d.polygon(pts,fill="#32A852")
    d.polygon([(176,145),(273,229),(230,256),(176,205)],fill="#3F8CF4")
    d.polygon([(176,307),(230,256),(273,283),(176,367)],fill="#F9C13D")
    d.polygon([(230,256),(273,229),(372,256),(273,283)],fill="#EF4A49")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def gmail(name):
    im=rounded_gradient("#F8F5F3","#EAE7E6"); d=ImageDraw.Draw(im)
    d.rounded_rectangle((135,165,377,350),34,fill="#F4F1EE")
    d.line((145,180,256,270,367,180),fill="#EA4335",width=35,joint="curve")
    d.line((145,180,145,340),fill="#C5221F",width=28)
    d.line((367,180,367,340),fill="#C5221F",width=28)
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def calendar(name):
    im=rounded_gradient("#F7F4F5","#E8E4E8"); d=ImageDraw.Draw(im)
    d.rounded_rectangle((132,120,380,384),42,fill="#F9F7F4")
    d.rounded_rectangle((132,120,380,198),42,fill="#3498F5")
    f=font("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",115)
    txt="31"; bb=d.textbbox((0,0),txt,font=f); d.text(((512-(bb[2]-bb[0]))/2,218),txt,font=f,fill="#20385A")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def clock(name):
    im=rounded_gradient("#F7F2EF","#E6E1DE"); d=ImageDraw.Draw(im)
    d.ellipse((137,137,375,375),fill="#F7F4F0",outline="#D7D0CB",width=4)
    d.line((256,256,256,183),fill="#202B3A",width=12)
    d.line((256,256,318,294),fill="#202B3A",width=12)
    d.ellipse((244,244,268,268),fill="#E74B4B")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def calculator(name):
    im=rounded_gradient("#7D8897","#4D5868"); d=ImageDraw.Draw(im)
    for r in range(2):
      for c in range(2):
        x=158+c*118;y=157+r*118
        d.rounded_rectangle((x,y,x+86,y+86),18,fill="#F4F2EE" if (r,c)==(1,1) else "#667180")
    f=font("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",58)
    syms=[("+",158,150),("−",277,150),("×",158,268),("=",285,268)]
    for s,x,y in syms:d.text((x+20,y+10),s,font=f,fill="#F8F6F2" if s!="=" else "#37404A")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def weather(name):
    im=rounded_gradient("#46C6F5","#168DE8"); d=ImageDraw.Draw(im)
    d.ellipse((245,150,345,250),fill="#FFD34D")
    d.rounded_rectangle((145,245,360,330),42,fill="#F6F7F4")
    d.ellipse((160,210,275,315),fill="#F6F7F4"); d.ellipse((235,190,345,315),fill="#F6F7F4")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def lens(name):
    im=rounded_gradient("#F8F5F2","#E9E6E4"); d=ImageDraw.Draw(im)
    cols=["#4285F4","#EA4335","#FBBC05","#34A853"]
    d.line((165,205,165,160,225,160),fill=cols[0],width=28)
    d.line((347,205,347,160,287,160),fill=cols[1],width=28)
    d.line((165,307,165,352,225,352),fill=cols[2],width=28)
    d.line((347,307,347,352,287,352),fill=cols[3],width=28)
    d.ellipse((218,218,294,294),fill="#4285F4")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def assistant(name):
    im=rounded_gradient("#FAF6F5","#EEE9E7"); d=ImageDraw.Draw(im)
    for x,y,r,c in [(218,218,55,"#4285F4"),(303,231,35,"#EA4335"),(288,303,24,"#FBBC05"),(214,312,18,"#34A853")]:
        d.ellipse((x-r,y-r,x+r,y+r),fill=c)
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

def meet(name):
    im=rounded_gradient("#F8F5F2","#E9E6E4"); d=ImageDraw.Draw(im)
    d.rounded_rectangle((155,180,305,330),28,fill="#34A853")
    d.polygon([(305,218),(380,175),(380,335),(305,292)],fill="#4285F4")
    d.rectangle((155,180,230,255),fill="#FBBC05"); d.rectangle((230,255,305,330),fill="#EA4335")
    im.save(os.path.join(OUT,name+".webp"),"WEBP",lossless=True,method=6)

# Base icon definitions: name, hex codepoint, bg top, bg bottom, brand?
defs = [
("phone","f095","#54DB93","#19AE65",False),("contacts","f007","#55C7F8","#168AE6",False),
("messages","f075","#66D794","#21AD68",False),("whatsapp","f232","#55D57A","#19B94E",True),
("camera","f030","#F7F3F1","#D8D5D5",False),("settings","f013","#8B98AA","#596677",False),
("youtube","f167","#FF776E","#E83C32",True),("maps","f3c5","#62D897","#21B46A",False),
("notes","f249","#FFD777","#F4AE2B",False),("files","f07b","#58C9F9","#198EE9",False),
("music","f001","#FF6680","#E83450",False),("videos","f04b","#AF57F5","#7131D8",False),
("recorder","f130","#FAF6F5","#EDE9E8",False),("compass","f14e","#F6EFF2","#D6DCE3",False),
("internet","f0ac","#55C4FB","#167FE5",False),("contacts_alt","f2b9","#EFE5DC","#D3BDA7",False),
("my_files","f07c","#FFD777","#F3AC27",False),("theme_store","f1fc","#FF7A6E","#D73DD5",False),
("security","f3ed","#54D49A","#1CAE70",False),("wallet","f555","#ECE8E5","#D6DADB",False),
("translate","f1ab","#3DAEF5","#1477D7",False),("fm_radio","f8d7","#FF6A70","#E63D46",False),
("podcasts","f2ce","#F8F5F1","#E4E3E2",False),("keep_notes","f0eb","#FFD66E","#F5AE25",False),
("tasks","f00c","#FBF5F5","#EDE6EB",False),("game_center","f11b","#FF756A","#E43B34",False),
("youtube_music","f144","#F24B51","#D91722",False),("find_device","f3c5","#67DC87","#21B55D",False),
("sheets","f00a","#55CF93","#169C5A",False),("docs","f15c","#52B6F7","#2377DD",False),
("slides","f1c4","#FFD467","#EFA627",False),("news","f1ea","#E5EEEF","#BFD5D8",False),
("tips","f0eb","#FFD66B","#F5AE28",False)
]
for args in defs: icon_font(*args)
chrome("chrome"); flower("gallery"); flower("photos"); flower("photos_alt")
playstore("play_store"); gmail("gmail"); calendar("calendar"); clock("clock"); calculator("calculator")
weather("weather"); weather("weather_alt"); lens("lens"); assistant("voice_assistant"); meet("meet")
# Drive: stylized brand glyph
icon_font("drive","f3aa","#FAF6F3","#E7E2E0",True,(58,161,109,255))

# Fallback masking resources for unmapped apps.
# These keep every stock app visually consistent without black square corners.
for idx,(c1,c2) in enumerate([
    ("#69D8A1","#24B76D"),
    ("#65C9F6","#258BE1"),
    ("#FFD77C","#F4AD2E"),
    ("#F5ECE8","#DCD6D3")
], start=1):
    bg = rounded_gradient(c1,c2)
    bg.save(os.path.join(OUT,f"iconback_{idx}.webp"),"WEBP",lossless=True,method=6)

mask = Image.new("RGBA",(512,512),(0,0,0,0))
md = ImageDraw.Draw(mask)
md.rounded_rectangle((30,26,482,478),118,fill=(255,255,255,255))
mask.save(os.path.join(OUT,"iconmask.webp"),"WEBP",lossless=True,method=6)

# 192px copies for launchers that prefer smaller resources
SMALL=os.path.join(os.path.dirname(OUT),"drawable")
os.makedirs(SMALL,exist_ok=True)
for fn in os.listdir(OUT):
    if fn.endswith(".webp") and not fn.startswith("wallpaper_"):
        im=Image.open(os.path.join(OUT,fn)).resize((192,192),Image.Resampling.LANCZOS)
        im.save(os.path.join(SMALL,fn),"WEBP",lossless=True,method=6)

def hexmix(a,b,t):
    aa=tuple(int(a[i:i+2],16) for i in (1,3,5)); bb=tuple(int(b[i:i+2],16) for i in (1,3,5))
    return tuple(int(aa[i]*(1-t)+bb[i]*t) for i in range(3))

def wallpaper(dark=False, lock=False):
    W,H=1080,2400
    im=Image.new("RGB",(W,H))
    p=im.load()
    c1,c2=("#071321","#102841") if dark else ("#45B7F5","#EEF8FF")
    for y in range(H):
        t=y/(H-1); col=hexmix(c1,c2,t)
        for x in range(W): p[x,y]=col
    d=ImageDraw.Draw(im)
    if not dark:
        # soft cloud blobs and modern abstract buildings
        for cx,cy,s in [(115,330,130),(910,430,110),(820,1150,150),(180,1430,140)]:
            layer=Image.new("RGBA",(W,H),(0,0,0,0)); ld=ImageDraw.Draw(layer)
            for dx,dy,rr in [(-70,5,s*.55),(0,-15,s*.7),(75,10,s*.5)]:
                ld.ellipse((cx+dx-rr,cy+dy-rr,cx+dx+rr,cy+dy+rr),fill=(255,255,255,150))
            layer=layer.filter(ImageFilter.GaussianBlur(28))
            im=Image.alpha_composite(im.convert("RGBA"),layer).convert("RGB"); d=ImageDraw.Draw(im)
        d.rounded_rectangle((-70,1550,310,2440),90,fill=(246,249,249))
        d.rounded_rectangle((800,1460,1160,2440),90,fill=(245,248,247))
        for yy in [1630,1850,2070]:
            d.rectangle((20,yy,260,yy+18),fill=(215,226,228))
            d.rectangle((835,yy-50,1040,yy-32),fill=(215,226,228))
    # central house + leaf mark, top area left clean for clock/widgets
    y0=980 if lock else 810
    if dark: y0=930
    house_col="#E9F2F5" if dark else "#278ECF"
    leaf_col="#32D79A" if dark else "#3EBA6F"
    d.polygon([(360,y0+135),(540,y0),(720,y0+135),(680,y0+185),(540,y0+80),(400,y0+185)],fill=house_col)
    d.rounded_rectangle((420,y0+145,660,y0+375),32,fill=(235,242,244) if dark else (247,250,249))
    d.rectangle((505,y0+230,555,y0+280),fill="#47657A")
    d.rectangle((570,y0+230,620,y0+280),fill="#47657A")
    d.ellipse((540,y0+205,790,y0+445),fill=leaf_col)
    d.pieslice((500,y0+180,800,y0+455),85,255,fill=(7,19,33) if dark else (238,248,252))
    # titles
    brand_font=font("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",72 if not dark else 62)
    sub_font=font("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",28)
    title="NEW ALI TRADERS"; bb=d.textbbox((0,0),title,font=brand_font)
    d.text(((W-(bb[2]-bb[0]))/2,y0+470),title,font=brand_font,fill=(235,242,247) if dark else (26,68,101))
    sub="BUILDING MATERIAL SUPPLIER & CONTRACTOR"; bb=d.textbbox((0,0),sub,font=sub_font)
    d.text(((W-(bb[2]-bb[0]))/2,y0+565),sub,font=sub_font,fill=(130,158,178) if dark else (48,83,106))
    # Bengali if Noto present
    bn_paths=["/usr/share/fonts/truetype/noto/NotoSansBengali-Regular.ttf","/usr/share/fonts/opentype/noto/NotoSansBengali-Regular.ttf"]
    bn_font=None
    for bp in bn_paths:
        if os.path.exists(bp):
            bn_font=font(bp,42); break
    if bn_font:
        bn="ভালো নির্মাণ • নিরাপদ ভবিষ্যৎ"; bb=d.textbbox((0,0),bn,font=bn_font)
        d.text(((W-(bb[2]-bb[0]))/2,y0+625),bn,font=bn_font,fill=(43,153,101) if not dark else (78,219,169))
    return im

wallpaper(False,False).save(os.path.join(OUT,"wallpaper_home.webp"),"WEBP",quality=88,method=6)
wallpaper(False,True).save(os.path.join(OUT,"wallpaper_lock.webp"),"WEBP",quality=88,method=6)
wallpaper(True,True).save(os.path.join(OUT,"wallpaper_aod.webp"),"WEBP",quality=86,method=6)
print("Generated", len([x for x in os.listdir(OUT) if x.endswith(".webp")]), "drawable-nodpi assets")
