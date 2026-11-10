import requests, os, datetime, sys

# The value of phpdisk_info in the Cookie
cookie_phpdisk_info = os.environ.get('phpdisk_info')
# The value of ylogin in the Cookie
cookie_ylogin = os.environ.get('ylogin')

# Request headers
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.72 Safari/537.36 Edg/89.0.774.45',
    'Accept-Language': 'zh-CN,zh;q=0.9',
    'Referer': 'https://pc.woozooo.com/account.php?action=login'
}

# Cookies
cookie = {
    'ylogin': cookie_ylogin,
    'phpdisk_info': cookie_phpdisk_info
}


# Log printing
def log(msg):
    utc_time = datetime.datetime.utcnow()
    china_time = utc_time + datetime.timedelta(hours=8)
    print(f"[{china_time.strftime('%Y.%m.%d %H:%M:%S')}] {msg}")


# Check whether we are logged in
def login_by_cookie():
    url_account = "https://pc.woozooo.com/account.php"
    if cookie['phpdisk_info'] is None:
        log('ERROR: Please specify the value of phpdisk_info in the Cookie!')
        return False
    if cookie['ylogin'] is None:
        log('ERROR: Please specify the value of ylogin in the Cookie!')
        return False
    res = requests.get(url_account, headers=headers, cookies=cookie, verify=True)
    # Identify the signed-out page by its login form endpoint (the same
    # account.php?action=login URL used as the Referer above) instead of the
    # page's former Chinese title text.
    if 'account.php?action=login' in res.text:
        log('ERROR: Login failed, please update the Cookie')
        return False
    else:
        log('Login successful')
        return True


# Upload a file
def upload_file(file_dir, folder_id):
    file_name = os.path.basename(file_dir)
    url_upload = "https://up.woozooo.com/fileup.php"
    headers['Referer'] = f'https://up.woozooo.com/mydisk.php?item=files&action=index&u={cookie_ylogin}'
    post_data = {
        "task": "1",
        "folder_id": folder_id,
        "id": "WU_FILE_0",
        "name": file_name,
    }
    files = {'upload_file': (file_name, open(file_dir, "rb"), 'application/octet-stream')}
    res = requests.post(url_upload, data=post_data, files=files, headers=headers, cookies=cookie, timeout=120).json()
    log(f"{file_dir} -> {res['info']}")
    return res['zt'] == 1


# Upload the files inside a folder
def upload_folder(folder_dir, folder_id):
    file_list = sorted(os.listdir(folder_dir), reverse=True)
    for file in file_list:
        path = os.path.join(folder_dir, file)
        if os.path.isfile(path):
            upload_file(path, folder_id)
        else:
            upload_folder(path, folder_id)


# Upload
def upload(dir, folder_id):
    if dir is None:
        log('ERROR: Please specify the path of the file to upload')
        return
    if folder_id is None:
        log('ERROR: Please specify the Lanzouyun folder id')
        return
    if os.path.isfile(dir):
        upload_file(dir, str(folder_id))
    else:
        upload_folder(dir, str(folder_id))


if __name__ == '__main__':
    argv = sys.argv[1:]
    if len(argv) != 2:
        log('ERROR: Invalid arguments, please retry in this format\npython lzy_web.py <path to upload> <Lanzouyun folder id>')
    # Path to upload
    upload_path = argv[0]
    # Lanzouyun folder id
    lzy_folder_id = argv[1]
    if login_by_cookie():
        upload(upload_path, lzy_folder_id)