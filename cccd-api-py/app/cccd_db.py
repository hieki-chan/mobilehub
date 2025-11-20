import os
import gspread
from oauth2client.service_account import ServiceAccountCredentials

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SERVICE_FILE = os.path.join(BASE_DIR, "service_account.json")

scope = [
    "https://spreadsheets.google.com/feeds",
    "https://www.googleapis.com/auth/drive"
]

creds = ServiceAccountCredentials.from_json_keyfile_name(SERVICE_FILE, scope)
client = gspread.authorize(creds)

sheet = client.open_by_url(
    "https://docs.google.com/spreadsheets/d/1bnixEY1AX2oj3h-syNZF8iTlDozXSK09Z40i8tkIU14/edit?usp=sharing"
)
worksheet = sheet.sheet1

rows = worksheet.get_all_values()  # list[list[str]]
headers = rows[0]
CCCD_DB = {}

for r in rows[1:]:
    row_dict = dict(zip(headers, r))
    cccd_raw = row_dict["Số CCCD"].strip()
    # đảm bảo luôn 12 số, thêm 0 đầu nếu thiếu
    if cccd_raw.isdigit():
        cccd_raw = cccd_raw.zfill(12)
    CCCD_DB[cccd_raw] = row_dict

#print("CCCD DB initialized!", CCCD_DB)
print("CCCD DB initialized!")

def validate_cccd(cccd_number):
    return CCCD_DB.get(str(cccd_number).strip())
