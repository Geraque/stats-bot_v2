#!/usr/bin/env python3
import re

def convert_dates(text: str) -> str:
    """
    Функция заменяет даты формата dd.mm.yyyy на yyyy-mm-dd.
    """
    # Регулярное выражение для поиска дат в формате dd.mm.yyyy
    pattern = re.compile(r'(\d{2})\.(\d{2})\.(\d{4})')
    # Замена: \3 - год, \2 - месяц, \1 - день
    return pattern.sub(r'\3-\2-\1', text)

def main():
    filename = 'example.txt'

    # Чтение содержимого файла
    try:
        with open(filename, 'r', encoding='utf-8') as file:
            content = file.read()
    except Exception as e:
        print(f'Ошибка при чтении файла {filename}: {e}')
        return

    # Замена дат
    updated_content = convert_dates(content)

    # Запись изменённого содержимого обратно в тот же файл
    try:
        with open(filename, 'w', encoding='utf-8') as file:
            file.write(updated_content)
        print('Файл успешно обновлён.')
    except Exception as e:
        print(f'Ошибка при записи в файл {filename}: {e}')

if __name__ == '__main__':
    main()
