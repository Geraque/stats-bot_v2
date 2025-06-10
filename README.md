# CS Match Tracker

CS Match Tracker is an application designed for storing and managing data about CS (Counter-Strike) matches in a database. The application provides tools to parse data from various external sources, store it in the database, and interact with it through multiple features, including an API, an Excel file, and a Telegram bot.

## Features

- **Data Parsing**: Automatically parse and store match data from various external sources into the database.
- **API for Data Access**: Access and manage match history stored in the database using API requests.
- **Excel File Integration**: View and edit the match history through a dynamically updated Excel file.
- **Telegram Bot**: Easily browse the data and interact with the match history using a Telegram bot.

## How It Works

1. **Data Collection and Parsing**:
    - The app includes a parser that collects match data from different sources (configurable based on your needs).
    - The parsed data is stored in a structured format in the database.

2. **Data Management**:
    - The API allows direct interaction with the match history in the database. You can retrieve, update, or delete specific data entries.
    - Match history is also available in an Excel document that remains synced with the database. You can make changes directly in the file to update the database.

3. **Telegram Bot**:
    - The integrated Telegram bot provides an easy way to access match history.
    - Users can query match data or get updates through an intuitive chat interface.


TODO
1) Закрашивать дату в нужный цвет, в зависимости от моего рейтинга за день
2) Исправить подсчёт результата матча (если кто-то сдаётся, результат может быть другой)
3) Исправить PREMIER (Сделать как в mm, но на будущее убрать лишние карты)
4) Доделать импорт из excel