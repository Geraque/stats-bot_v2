# Задать имя файла
file_name = "example.txt"

# Чтение всех строк файла
with open(file_name, "r", encoding="utf-8") as file:
    lines = file.readlines()

# Если в файле есть хотя бы одна строка:
if lines:
    # Первая строка остаётся без изменений
    modified_lines = [lines[0]]
    # Для остальных строк выполняется замена пробелов на запятые
    for line in lines[1:]:
        modified_lines.append(line.replace("	", ","))
else:
    modified_lines = lines

# Запись изменённого содержимого обратно в тот же файл
with open(file_name, "w", encoding="utf-8") as file:
    file.writelines(modified_lines)

