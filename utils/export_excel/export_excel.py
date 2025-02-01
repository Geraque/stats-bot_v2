import openpyxl
from datetime import datetime

# Указать тип матча: "MATCH_MAKING", "PREMIER", "FACEIT" или "WINGMAN"
match_type = "MATCH_MAKING"

# Загрузка книги и выбор листа
workbook = openpyxl.load_workbook("statistics.xlsx", data_only=True)
worksheet = workbook["2025 mm"]

# Настройка игроков: номер колонки рейтинга и диапазон колонок статистики
players = {
    "DESMOND": {
        "rating": 2,  # столбец B
        "stats": list(range(7, 20))  # столбцы G (7) до S (19)
    },
    "BLACK_VISION": {
        "rating": 3,  # столбец C
        "stats": list(range(20, 33))  # столбцы T (20) до AF (32)
    },
    "GLOXINIA": {
        "rating": 4,  # столбец D
        "stats": list(range(33, 46))  # столбцы AG (33) до AS (45)
    }
}
# players = {
#     "DESMOND": {
#         "rating": 2,  # столбец B
#         "stats": list(range(7, 20))  # столбцы G (7) до S (19)
#     },
#     "BLACK_VISION": {
#         "rating": 3,  # столбец C
#         "stats": list(range(20, 33))  # столбцы T (20) до AF (32)
#     },
#     "B4ONE": {
#         "rating": 4,  # столбец D
#         "stats": list(range(33, 46))  # столбцы AG (33) до AS (45)
#     },
#     "GLOXINIA": {
#         "rating": 5,  # столбец E
#         "stats": list(range(46, 59))  # столбцы AT (46) до BF (58)
#     },
#     "NEKIT": {
#         "rating": 6,  # столбец F
#         "stats": list(range(59, 72))  # столбцы BG (59) до BS (71)
#     }
# }

# Список для накопления строк со значениями для SQL-запросов
inserts = []

# Переменная для хранения текущей даты матча
current_date = None

# Обход строк листа
for row in worksheet.iter_rows(min_row=1):
    cell_A = row[0]
    val_A = cell_A.value

    # Прерывание обхода, если это не первая строка и в столбце A встречается пустое значение,
    # "avg rating" или "KAD"
    if cell_A.row != 1 and (val_A is None or
                             (isinstance(val_A, str) and (val_A.strip() == "" or val_A.strip().lower() in ["avg rating", "kad"]))):
        break

    # Если значение в столбце A является датой (объект datetime)
    if isinstance(val_A, datetime):
        current_date = val_A.strftime("%d.%m.%Y")
        continue  # строка содержит только дату, статистика отсутствует
    # Если значение в столбце A является строкой в формате "дд.мм.гггг"
    elif isinstance(val_A, str):
        try:
            parsed_date = datetime.strptime(val_A.strip(), "%d.%m.%Y")
            current_date = parsed_date.strftime("%d.%m.%Y")
            continue  # строка содержит только дату
        except ValueError:
            # Если преобразование не удалось, предполагается, что значение – номер матча
            pass

    # Если дата матча ещё не установлена, пропустить строку
    if current_date is None:
        continue

    # Формирование SQL-строк для каждого игрока
    for player, cols in players.items():
        # Чтение рейтинга из соответствующего столбца (индексы начинаются с 0)
        rating_cell = row[cols["rating"] - 1]
        rating_val = rating_cell.value

        # Если рейтинг не указан, статистика для данного игрока не обрабатывается
        if rating_val is None or (isinstance(rating_val, str) and rating_val.strip() == ""):
            continue

        # Формирование SQL-строки в зависимости от типа матча
        if match_type == "MATCH_MAKING" or match_type == "PREMIER":
            # Сбор значений статистики для игрока
            stats_vals = []
            for col_index in cols["stats"]:
                cell_val = row[col_index - 1].value
                # Если значение отсутствует, использовать 0
                stats_vals.append(0 if cell_val is None else cell_val)

            # Формирование строки с данными:
            # ('PLAYER', rating, smoke_kill, open_kill, three_kill, four_kill, ace, flash, trade,
            #  wall_bang, clutch_one, clutch_two, clutch_three, clutch_four, clutch_five, 'date', 'MATCH_MAKING')
            values_tuple = f"('{player}', {rating_val}, " + \
                           ",".join(str(s) for s in stats_vals) + \
                           f",'{current_date}', '{match_type}')"
        elif match_type == "WINGMAN":
            # Для wingman учитывается только рейтинг
            values_tuple = f"('{player}', {rating_val}, '{current_date}', '{match_type}')"
        else:
            # Если тип матча не распознан, пропустить
            continue

        inserts.append(values_tuple)

# Запись SQL-скрипта в файл, если сформированы строки
if inserts:
    with open("output.txt", "w", encoding="utf-8") as f:
        if match_type == "MATCH_MAKING" or match_type == "PREMIER":
            header = ("INSERT INTO public.matches (player_name, rating, smoke_kill, open_kill, three_kill, "
                      "four_kill, ace, flash, trade, wall_bang, clutch_one, clutch_two, clutch_three, "
                      "clutch_four, clutch_five, \"date\", \"type\") VALUES\n")
        elif match_type == "WINGMAN":
            header = "INSERT INTO public.matches (player_name, rating, \"date\", \"type\") VALUES\n"
        else:
            header = ""
        f.write(header)
        f.write(",\n".join(inserts))
        f.write(";\n")
