# ocr_cccd_only.py
import easyocr
import cv2
import numpy as np
from io import BytesIO
import re

use_gpu = False
reader = easyocr.Reader(['vi', 'en'], gpu=use_gpu)

def extract_cccd_from_image(file_like):
    """
    Trả về chỉ số CCCD, ignore các field khác
    """
    if isinstance(file_like, BytesIO):
        file_bytes = np.frombuffer(file_like.read(), np.uint8)
    elif isinstance(file_like, (bytes, bytearray)):
        file_bytes = np.frombuffer(file_like, np.uint8)
    else:
        raise ValueError("Phải truyền BytesIO hoặc bytes")

    mat = cv2.imdecode(file_bytes, cv2.IMREAD_COLOR)
    if mat is None:
        raise ValueError("Không đọc được ảnh.")

    result = reader.readtext(mat)
    lines = [line[1].strip() for line in result]
    full_text = " ".join(lines)

    m = re.search(r"\b\d{9,12}\b", full_text)
    cccd_number = m.group() if m else None

    return {"cccd": cccd_number}
