import requests

# Конфигурационные параметры (заменить на собственные значения)
API_KEY = 'd88f8ad4-24a1-46e2-94a2-5f53f4922cfe'
FACEIT_API_BASE = 'https://open.faceit.com/data/v4'
FACEIT_DOWNLOAD_API = 'https://www.faceit.com/api/download/v2/demos/download-url'
USER_NICKNAME = 'wesdia'
GAME = 'cs2'  # идентификатор игры (для CS2)

# Заголовки для запросов к API
HEADERS = {
    'Authorization': f'Bearer {API_KEY}',
    'Content-Type': 'application/json'
}

def get_player_details(nickname):
    """
    Получение данных об игроке по никнейму.
    """
    url = f"{FACEIT_API_BASE}/players?nickname={nickname}&game={GAME}"
    response = requests.get(url, headers=HEADERS)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Ошибка при получении данных игрока: {response.status_code}")
        return None

def get_player_match_history(player_id):
    """
    Получение истории матчей игрока (limit=1 для последнего матча).
    """
    url = f"{FACEIT_API_BASE}/players/{player_id}/history?game={GAME}&limit=1"
    response = requests.get(url, headers=HEADERS)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Ошибка при получении истории матчей: {response.status_code}")
        return None

def get_match_details(match_id):
    """
    Получение деталей матча по его идентификатору.
    """
    url = f"{FACEIT_API_BASE}/matches/{match_id}"
    response = requests.get(url, headers=HEADERS)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Ошибка при получении деталей матча: {response.status_code}")
        return None

def get_signed_demo_url(resource_url):
    """
    Отправка POST-запроса для получения подписанного URL демки.
    В теле запроса передаётся исходный URL демки в параметре resource_url.
    Из ответа извлекается значение payload.download_url.
    """
    print(resource_url)
    payload = {"resource_url": resource_url}
    response = requests.post(FACEIT_DOWNLOAD_API, headers=HEADERS, json=payload)
    if response.status_code == 200:
        data = response.json()
        signed_url = data.get('payload', {}).get('download_url')
        if signed_url:
            print(f"Получен подписанный URL: {signed_url}")
            return signed_url
        else:
            print("Подписанный URL не найден в ответе")
            return None
    else:
        print(f"Ошибка при запросе подписанного URL: {response.status_code}")
        return None

def download_demo(demo_url, output_file):
    """
    Скачивание файла демки по указанному URL и сохранение в output_file.
    """
    response = requests.get(demo_url, stream=True)
    if response.status_code == 200:
        with open(output_file, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                if chunk:
                    f.write(chunk)
        print(f"Файл демки скачан и сохранён: {output_file}")
    else:
        print(f"Ошибка при скачивании демки: {response.status_code}")

def main():
    player_data = get_player_details(USER_NICKNAME)
    if not player_data:
        return

    player_id = player_data.get('player_id')
    if not player_id:
        print("Идентификатор игрока не найден")
        return

    history = get_player_match_history(player_id)
    if not history or not history.get('items'):
        print("История матчей не найдена или произошла ошибка")
        return

    last_match = history['items'][0]
    match_id = last_match.get('match_id')
    if not match_id:
        print("Идентификатор последнего матча не найден")
        return

    match_details = get_match_details(match_id)
    if not match_details:
        return

    demo_urls = match_details.get('demo_url')
    if demo_urls and isinstance(demo_urls, list) and len(demo_urls) > 0:
        original_demo_url = demo_urls[0]
        print(f"Исходный URL демки: {original_demo_url}")

        # Получение подписанного URL через POST-запрос
        signed_url = get_signed_demo_url(original_demo_url)
        if signed_url:
            output_filename = "last_match.dem.gz"
            download_demo(signed_url, output_filename)
        else:
            print("Не удалось получить подписанный URL демки")
    else:
        print("Демка не доступна для данного матча")

if __name__ == "__main__":
    main()
