import math
import os
from PIL import Image, ImageDraw, ImageFont

OUTPUT_DIR = r"s:\graph hackathon\video-assets"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Fonts
FONT_TITLE = ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 46)
FONT_SUBTITLE = ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 24)
FONT_HEADING = ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 26)
FONT_BODY = ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 20)
FONT_BODY_BOLD = ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 20)
FONT_CODE = ImageFont.truetype("C:/Windows/Fonts/consola.ttf", 16)
FONT_CODE_BOLD = ImageFont.truetype("C:/Windows/Fonts/consolab.ttf" if os.path.exists("C:/Windows/Fonts/consolab.ttf") else "C:/Windows/Fonts/consola.ttf", 17)
FONT_SMALL = ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 15)
FONT_BADGE = ImageFont.truetype("C:/Windows/Fonts/segoeuib.ttf", 14)

def create_base_canvas(w=1920, h=1080):
    img = Image.new("RGB", (w, h), (9, 13, 24))
    draw = ImageDraw.Draw(img)
    
    # Subtle gradient
    for y in range(h):
        r = int(9 + (y / h) * 12)
        g = int(14 + (y / h) * 16)
        b = int(26 + (y / h) * 28)
        draw.line([(0, y), (w, y)], fill=(r, g, b))
        
    # Subtle grid dots
    for x in range(40, w, 40):
        for y in range(40, h, 40):
            draw.point((x, y), fill=(24, 34, 60))
            
    return img, draw

def draw_header(draw, title_tag, subtitle_text):
    # Brand logo pill
    draw.rounded_rectangle([60, 40, 310, 85], radius=12, fill=(15, 23, 42), outline=(37, 99, 235), width=2)
    # Draw mini shield icon
    draw.polygon([(82, 53), (94, 53), (98, 62), (88, 73), (78, 62)], fill=(56, 189, 248))
    draw.text((106, 50), "DependencyGuard AI", font=FONT_BODY_BOLD, fill=(255, 255, 255))
    
    # Section Tag
    draw.rounded_rectangle([330, 45, 560, 80], radius=8, fill=(30, 41, 59), outline=(71, 85, 105), width=1)
    draw.text((345, 52), title_tag, font=FONT_BADGE, fill=(56, 189, 248))
    
    # Subtitle / Time
    draw.text((60, 105), subtitle_text, font=FONT_SUBTITLE, fill=(148, 163, 184))
    
    # Status badges on right
    draw.rounded_rectangle([1580, 45, 1860, 80], radius=8, fill=(15, 23, 42), outline=(16, 185, 129), width=1)
    draw.ellipse([1598, 59, 1608, 69], fill=(52, 211, 153))
    draw.text((1618, 52), "FalkorDB Engine: ACTIVE", font=FONT_BADGE, fill=(52, 211, 153))

# ==============================================================================
# SCENE 1: The Invisible Supply Chain Risk
# ==============================================================================
def generate_scene_1():
    img, draw = create_base_canvas()
    draw_header(draw, "SCENE 1: THE PROBLEM", "Hidden Transitive Vulnerabilities in Modern Microservices")

    # Big headline
    draw.text((60, 145), "The Invisible Software Supply-Chain Threat", font=FONT_TITLE, fill=(255, 255, 255))
    
    # Top metrics bar
    metrics = [
        ("Tier-1 Applications", "5 Scanned", (59, 130, 246)),
        ("Direct Packages", "14 Verified", (16, 185, 129)),
        ("Transitive Packages", "28 Discovered", (168, 85, 247)),
        ("Hidden Threat Depth", "3 Hops Deep", (239, 68, 68))
    ]
    mx = 60
    for label, val, color in metrics:
        draw.rounded_rectangle([mx, 220, mx + 240, 290], radius=10, fill=(15, 23, 42), outline=(30, 41, 59), width=1)
        draw.text((mx + 15, 230), label, font=FONT_SMALL, fill=(148, 163, 184))
        draw.text((mx + 15, 252), val, font=FONT_HEADING, fill=color)
        mx += 260

    # Draw 3-Tier Multi-Hop Graph Demonstration
    # Tier 1 Apps (Left)
    t1_nodes = [
        (100, 370, "Payment Service", "Tier-1 Core Service", (14, 165, 233)),
        (100, 540, "Auth Gateway", "Tier-1 Perimeter API", (14, 165, 233)),
        (100, 710, "Customer Portal", "Tier-1 Web Frontend", (14, 165, 233))
    ]
    for x, y, name, sub, col in t1_nodes:
        draw.rounded_rectangle([x, y, x + 250, y + 100], radius=14, fill=(15, 23, 42), outline=col, width=2)
        draw.rounded_rectangle([x + 18, y + 20, x + 58, y + 42], radius=4, fill=(14, 165, 233))
        draw.text((x + 22, y + 22), "APP", font=FONT_BADGE, fill=(15, 23, 42))
        draw.text((x + 66, y + 20), name, font=FONT_BODY_BOLD, fill=(255, 255, 255))
        draw.text((x + 20, y + 60), sub, font=FONT_SMALL, fill=(148, 163, 184))

    # Tier 2 Packages (Middle)
    t2_nodes = [
        (600, 420, "auth-core v1.2.0", "Direct Dependency", (147, 51, 234)),
        (600, 650, "web-router v2.4.1", "Direct Dependency", (147, 51, 234))
    ]
    for x, y, name, sub, col in t2_nodes:
        draw.rounded_rectangle([x, y, x + 250, y + 100], radius=14, fill=(24, 24, 47), outline=col, width=2)
        draw.rounded_rectangle([x + 18, y + 20, x + 58, y + 42], radius=4, fill=(147, 51, 234))
        draw.text((x + 22, y + 22), "PKG", font=FONT_BADGE, fill=(255, 255, 255))
        draw.text((x + 66, y + 20), name, font=FONT_BODY_BOLD, fill=(255, 255, 255))
        draw.text((x + 20, y + 60), sub, font=FONT_SMALL, fill=(192, 132, 252))

    # Tier 3 Transitive Packages (Right)
    t3_nodes = [
        (1080, 470, "crypto-vault v0.9.1", "Transitive (Hop 2)", (217, 70, 239)),
        (1080, 680, "jwt-handler v1.4.3", "Transitive (Hop 2)", (217, 70, 239))
    ]
    for x, y, name, sub, col in t3_nodes:
        draw.rounded_rectangle([x, y, x + 250, y + 100], radius=14, fill=(30, 27, 58), outline=col, width=2)
        draw.rounded_rectangle([x + 18, y + 20, x + 58, y + 42], radius=4, fill=(217, 70, 239))
        draw.text((x + 22, y + 22), "LIB", font=FONT_BADGE, fill=(255, 255, 255))
        draw.text((x + 66, y + 20), name, font=FONT_BODY_BOLD, fill=(255, 255, 255))
        draw.text((x + 20, y + 60), sub, font=FONT_SMALL, fill=(240, 171, 252))

    # Critical Vulnerability Node (Far Right Deep Threat)
    vx, vy = 1520, 520
    # Shockwave circles
    for r in [170, 140, 110, 80]:
        draw.ellipse([vx + 130 - r, vy + 75 - r, vx + 130 + r, vy + 75 + r], outline=(239, 68, 68), width=2)
    draw.rounded_rectangle([vx, vy, vx + 280, vy + 150], radius=16, fill=(45, 12, 18), outline=(239, 68, 68), width=3)
    
    # Warning tag
    draw.rounded_rectangle([vx + 20, vy + 18, vx + 90, vy + 40], radius=4, fill=(239, 68, 68))
    draw.text((vx + 25, vy + 20), "ALERT", font=FONT_BADGE, fill=(255, 255, 255))
    draw.text((vx + 100, vy + 18), "CVE-2024-45321", font=FONT_HEADING, fill=(248, 113, 113))
    
    draw.text((vx + 20, vy + 60), "Critical RCE / Auth Bypass", font=FONT_BODY, fill=(254, 202, 202))
    draw.text((vx + 20, vy + 95), "CVSS 9.8  |  3 Hops Deep", font=FONT_CODE_BOLD, fill=(239, 68, 68))

    # Connectors (Edges)
    # T1 to T2
    draw.line([(350, 420), (600, 470)], fill=(56, 189, 248), width=3)
    draw.line([(350, 590), (600, 470)], fill=(56, 189, 248), width=3)
    draw.line([(350, 760), (600, 700)], fill=(56, 189, 248), width=3)

    # T2 to T3
    draw.line([(850, 470), (1080, 520)], fill=(192, 132, 252), width=3)
    draw.line([(850, 700), (1080, 730)], fill=(192, 132, 252), width=3)

    # T3 to Vulnerability (Dangerous link in red)
    draw.line([(1330, 520), (1520, 595)], fill=(239, 68, 68), width=4)

    # Bottom Callout Banner
    draw.rounded_rectangle([60, 910, 1860, 1020], radius=14, fill=(15, 23, 42), outline=(37, 99, 235), width=2)
    draw.text((90, 930), "[PROBLEM STATEMENT]", font=FONT_HEADING, fill=(56, 189, 248))
    draw.text((90, 970), "Standard flat package manifests cannot trace multi-hop transitive exposure. DependencyGuard AI uses FalkorDB graph traversals to expose every hidden attack vector.", font=FONT_BODY, fill=(226, 232, 240))

    img.save(os.path.join(OUTPUT_DIR, "scene-1-supply-chain-risk.jpg"), quality=95)
    print("Scene 1 regenerated cleanly.")

# ==============================================================================
# SCENE 5: Graph-Based Remediation & 1-Click Rollback
# ==============================================================================
def generate_scene_5():
    img, draw = create_base_canvas()
    draw_header(draw, "SCENE 5: REMEDIATION & ROLLBACK", "Atomic Graph Edge Rewiring & Instant Safe Recovery")

    # Big headline
    draw.text((60, 145), "Atomic Graph Remediation & Live Validation", font=FONT_TITLE, fill=(255, 255, 255))

    # Two-Panel Comparison Canvas
    # LEFT PANEL: BEFORE REMEDIATION (VULNERABLE)
    lx, ly = 60, 230
    lw, lh = 850, 650
    draw.rounded_rectangle([lx, ly, lx + lw, ly + lh], radius=16, fill=(17, 24, 39), outline=(239, 68, 68), width=2)
    
    # Left Header
    draw.rounded_rectangle([lx + 20, ly + 20, lx + 360, ly + 65], radius=8, fill=(45, 12, 18), outline=(239, 68, 68), width=1)
    draw.ellipse([lx + 35, ly + 37, lx + 47, ly + 49], fill=(239, 68, 68))
    draw.text((lx + 58, ly + 28), "BEFORE: Active Blast Radius", font=FONT_BODY_BOLD, fill=(248, 113, 113))

    # Left Nodes
    draw.rounded_rectangle([lx + 60, ly + 110, lx + 320, ly + 200], radius=12, fill=(15, 23, 42), outline=(14, 165, 233), width=2)
    draw.text((lx + 80, ly + 125), "Payment Service", font=FONT_BODY_BOLD, fill=(255, 255, 255))
    draw.text((lx + 80, ly + 160), "Tier-1 Core Application", font=FONT_SMALL, fill=(148, 163, 184))

    draw.rounded_rectangle([lx + 500, ly + 110, lx + 800, ly + 220], radius=12, fill=(45, 12, 18), outline=(239, 68, 68), width=2)
    draw.text((lx + 520, ly + 125), "auth-core v1.2.0", font=FONT_BODY_BOLD, fill=(248, 113, 113))
    draw.text((lx + 520, ly + 160), "[!] CVE-2024-45321 (Critical)", font=FONT_SMALL, fill=(254, 202, 202))
    draw.text((lx + 520, ly + 185), "CVSS 9.8 Severity", font=FONT_BADGE, fill=(239, 68, 68))

    # Red Disconnecting Edge
    draw.line([(lx + 320, ly + 155), (lx + 500, ly + 155)], fill=(239, 68, 68), width=4)
    draw.text((lx + 360, ly + 125), "DEPENDS_ON", font=FONT_BADGE, fill=(239, 68, 68))
    draw.text((lx + 365, ly + 170), "[X] SEVER EDGE", font=FONT_BADGE, fill=(248, 113, 113))

    # Blast Radius Meter Before
    draw.rounded_rectangle([lx + 60, ly + 270, lx + 790, ly + 430], radius=12, fill=(15, 23, 42), outline=(30, 41, 59), width=1)
    draw.text((lx + 90, ly + 290), "Risk Telemetry Before Remediation", font=FONT_HEADING, fill=(255, 255, 255))
    draw.text((lx + 90, ly + 340), "Blast Radius Score: 88.5 / 100", font=FONT_TITLE, fill=(239, 68, 68))
    draw.text((lx + 90, ly + 395), "Impacted Apps: Payment Service, Auth Gateway | Exposure: HIGH", font=FONT_BODY, fill=(148, 163, 184))

    # Cypher Action Box Before
    draw.rounded_rectangle([lx + 60, ly + 460, lx + 790, ly + 610], radius=10, fill=(10, 15, 30), outline=(51, 65, 85), width=1)
    draw.text((lx + 80, ly + 475), "// FalkorDB OpenCypher Mutation:", font=FONT_CODE_BOLD, fill=(148, 163, 184))
    draw.text((lx + 80, ly + 510), "MATCH (a:App)-[r:DEPENDS_ON]->(old:Pkg {name:'auth-core', ver:'1.2.0'})", font=FONT_CODE, fill=(248, 113, 113))
    draw.text((lx + 80, ly + 540), "DELETE r", font=FONT_CODE_BOLD, fill=(239, 68, 68))
    draw.text((lx + 80, ly + 570), "CREATE (a)-[:DEPENDS_ON {remediated: true}]->(new:Pkg {name:'auth-core', ver:'1.2.4'})", font=FONT_CODE, fill=(52, 211, 153))

    # RIGHT PANEL: AFTER REMEDIATION (PATCHED & SECURE)
    rx, ry = 980, 230
    rw, rh = 880, 650
    draw.rounded_rectangle([rx, ry, rx + rw, ry + rh], radius=16, fill=(17, 24, 39), outline=(16, 185, 129), width=2)

    # Right Header
    draw.rounded_rectangle([rx + 20, ry + 20, rx + 400, ry + 65], radius=8, fill=(6, 78, 59), outline=(16, 185, 129), width=1)
    draw.ellipse([rx + 35, ly + 37, rx + 47, ly + 49], fill=(52, 211, 153))
    draw.text((rx + 58, ry + 28), "AFTER: Rewired & Neutralized", font=FONT_BODY_BOLD, fill=(110, 231, 183))

    # Right Nodes
    draw.rounded_rectangle([rx + 60, ry + 110, rx + 320, ry + 200], radius=12, fill=(15, 23, 42), outline=(14, 165, 233), width=2)
    draw.text((rx + 80, ry + 125), "Payment Service", font=FONT_BODY_BOLD, fill=(255, 255, 255))
    draw.text((rx + 80, ry + 160), "Tier-1 Core Application", font=FONT_SMALL, fill=(148, 163, 184))

    draw.rounded_rectangle([rx + 520, ry + 110, rx + 820, ry + 220], radius=12, fill=(6, 78, 59), outline=(16, 185, 129), width=2)
    draw.text((rx + 540, ry + 125), "auth-core v1.2.4", font=FONT_BODY_BOLD, fill=(110, 231, 183))
    draw.text((rx + 540, ry + 160), "[SAFE] Verified Patched Release", font=FONT_SMALL, fill=(167, 243, 208))
    draw.text((rx + 540, ry + 185), "Zero Known CVEs | Clean", font=FONT_BADGE, fill=(52, 211, 153))

    # Green Connected Edge
    draw.line([(rx + 320, ry + 155), (rx + 520, ry + 155)], fill=(16, 185, 129), width=4)
    draw.text((rx + 360, ry + 125), "DEPENDS_ON", font=FONT_BADGE, fill=(52, 211, 153))
    draw.text((rx + 365, ry + 170), "[OK] REWIRED", font=FONT_BADGE, fill=(110, 231, 183))

    # Blast Radius Meter After
    draw.rounded_rectangle([rx + 60, ry + 270, rx + 820, ry + 430], radius=12, fill=(15, 23, 42), outline=(30, 41, 59), width=1)
    draw.text((rx + 90, ry + 290), "Risk Telemetry After Remediation", font=FONT_HEADING, fill=(255, 255, 255))
    draw.text((rx + 90, ry + 340), "Blast Radius Score: 0.0 / 100", font=FONT_TITLE, fill=(16, 185, 129))
    draw.text((rx + 90, ry + 395), "Impacted Apps: 0 | Supply-Chain Status: 100% PROTECTED", font=FONT_BODY, fill=(167, 243, 208))

    # Safety & Rollback Controls
    draw.rounded_rectangle([rx + 60, ry + 460, rx + 420, ry + 540], radius=10, fill=(5, 150, 105), outline=(16, 185, 129), width=1)
    draw.text((rx + 85, ry + 480), "[OK] Remediation Applied", font=FONT_HEADING, fill=(255, 255, 255))
    draw.text((rx + 85, ry + 512), "Graph Mutated & Verified", font=FONT_SMALL, fill=(209, 250, 229))

    draw.rounded_rectangle([rx + 450, ry + 460, rx + 820, ry + 540], radius=10, fill=(180, 83, 9), outline=(245, 158, 11), width=1)
    draw.text((rx + 475, ry + 480), "[ROLLBACK] 1-Click Snapshot", font=FONT_HEADING, fill=(255, 255, 255))
    draw.text((rx + 475, ry + 512), "Instant Zero-Downtime Revert", font=FONT_SMALL, fill=(254, 243, 199))

    # Compatibility check
    draw.rounded_rectangle([rx + 60, ry + 560, rx + 820, ry + 615], radius=10, fill=(15, 23, 42), outline=(37, 99, 235), width=1)
    draw.text((rx + 80, ry + 575), "Semantic Compatibility: 100% Non-breaking | No API Drift Detected", font=FONT_BODY_BOLD, fill=(56, 189, 248))

    # Bottom Callout Banner
    draw.rounded_rectangle([60, 910, 1860, 1020], radius=14, fill=(15, 23, 42), outline=(16, 185, 129), width=2)
    draw.text((90, 930), "[THE FALKORDB ADVANTAGE]", font=FONT_HEADING, fill=(52, 211, 153))
    draw.text((90, 970), "Unlike static security scans, FalkorDB allows live graph rewiring in memory and instant rollback snapshots without touching production source repositories.", font=FONT_BODY, fill=(226, 232, 240))

    img.save(os.path.join(OUTPUT_DIR, "scene-5-remediation-rollback.jpg"), quality=95)
    print("Scene 5 regenerated cleanly.")

if __name__ == "__main__":
    generate_scene_1()
    generate_scene_5()
