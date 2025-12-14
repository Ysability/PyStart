package com.example.myapplication12345678.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocalDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                login TEXT NOT NULL UNIQUE,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                avatar TEXT DEFAULT '🐍',
                secret_word TEXT DEFAULT ''
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                level TEXT NOT NULL,
                icon TEXT DEFAULT '🐍',
                lessons_count INTEGER DEFAULT 5
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE lessons (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                order_num INTEGER NOT NULL,
                duration_minutes INTEGER DEFAULT 10,
                FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE user_stats (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_login TEXT NOT NULL UNIQUE,
                completed_courses INTEGER NOT NULL DEFAULT 0,
                total_time_minutes INTEGER NOT NULL DEFAULT 0
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE user_lesson_progress (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_login TEXT NOT NULL,
                lesson_id INTEGER NOT NULL,
                completed INTEGER DEFAULT 0,
                completed_at TEXT,
                UNIQUE(user_login, lesson_id)
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE favorites (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_login TEXT NOT NULL,
                course_id INTEGER NOT NULL,
                UNIQUE(user_login, course_id)
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE support_messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_login TEXT NOT NULL,
                message TEXT NOT NULL,
                is_from_admin INTEGER DEFAULT 0,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE test_questions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_id INTEGER NOT NULL,
                question_text TEXT NOT NULL,
                option1 TEXT NOT NULL,
                option2 TEXT NOT NULL,
                option3 TEXT NOT NULL,
                option4 TEXT NOT NULL,
                correct_option INTEGER NOT NULL,
                FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
            );
            """.trimIndent()
        )

        insertInitialData(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // Начальный уровень - 4 курса
        val beginnerCourses = listOf(
            Triple("Основы Python", "Синтаксис, типы данных, переменные и операторы", "Начальный"),
            Triple("Условия и циклы", "Условные операторы if/else, циклы for и while", "Начальный"),
            Triple("Строки и списки", "Работа со строками, списками и их методами", "Начальный"),
            Triple("Функции", "Создание и использование функций, аргументы", "Начальный")
        )

        // Средний уровень - 4 курса
        val intermediateCourses = listOf(
            Triple("ООП в Python", "Классы, объекты, наследование, полиморфизм", "Средний"),
            Triple("Работа с файлами", "Чтение, запись файлов, контекстные менеджеры", "Средний"),
            Triple("Модули и пакеты", "Импорт модулей, создание пакетов, pip", "Средний"),
            Triple("Обработка ошибок", "Try/except, создание исключений, отладка", "Средний")
        )

        // Продвинутый уровень - 4 курса
        val advancedCourses = listOf(
            Triple("Декораторы", "Создание и применение декораторов функций", "Продвинутый"),
            Triple("Генераторы", "Генераторы, итераторы, yield", "Продвинутый"),
            Triple("Асинхронность", "Async/await, asyncio, параллельное выполнение", "Продвинутый"),
            Triple("Работа с API", "HTTP запросы, REST API, JSON", "Продвинутый")
        )

        val allCourses = beginnerCourses + intermediateCourses + advancedCourses

        // Уроки для каждого курса
        val lessonsData = mapOf(
            "Основы Python" to listOf(
                "Введение в Python" to "🐍 Python — один из самых популярных языков программирования в мире!\n\n📌 Почему Python?\n• Простой и понятный синтаксис\n• Огромное сообщество разработчиков\n• Используется в Data Science, веб-разработке, автоматизации\n\n🛠 Установка:\n1. Скачайте Python с python.org\n2. Установите IDE (PyCharm, VS Code)\n3. Проверьте: python --version\n\n💡 Первая программа:\nprint('Привет, мир!')",
                "Переменные и типы данных" to "📦 Переменные — это контейнеры для хранения данных.\n\n🔢 Основные типы:\n• int — целые числа: age = 25\n• float — дробные: price = 19.99\n• str — строки: name = 'Анна'\n• bool — логические: is_active = True\n\n🔍 Проверка типа:\ntype(age)  # <class 'int'>\n\n⚡ Динамическая типизация:\nx = 10      # int\nx = 'текст' # теперь str\n\n💡 Имена переменных: snake_case, без пробелов, не начинать с цифры.",
                "Операторы" to "🧮 Арифметические операторы:\n+ сложение: 5 + 3 = 8\n- вычитание: 5 - 3 = 2\n* умножение: 5 * 3 = 15\n/ деление: 5 / 2 = 2.5\n// целочисленное: 5 // 2 = 2\n% остаток: 5 % 2 = 1\n** степень: 2 ** 3 = 8\n\n⚖️ Сравнение:\n== равно, != не равно\n< > <= >=\n\n🔗 Логические:\nand — И (оба True)\nor — ИЛИ (хотя бы один True)\nnot — НЕ (инверсия)",
                "Ввод и вывод" to "📤 Вывод — print():\nprint('Привет!')  # Привет!\nprint('Сумма:', 2+2)  # Сумма: 4\n\n📥 Ввод — input():\nname = input('Ваше имя: ')\nprint(f'Привет, {name}!')\n\n⚠️ input() всегда возвращает строку!\nage = int(input('Возраст: '))\n\n✨ f-строки (форматирование):\nname = 'Иван'\nage = 25\nprint(f'{name}, вам {age} лет')\n\n🎨 Спецсимволы:\n\\n — новая строка\n\\t — табуляция",
                "Практика: Калькулятор" to "🧮 Создаём калькулятор!\n\n📝 Код:\na = float(input('Первое число: '))\nb = float(input('Второе число: '))\nop = input('Операция (+,-,*,/): ')\n\nif op == '+':\n    result = a + b\nelif op == '-':\n    result = a - b\nelif op == '*':\n    result = a * b\nelif op == '/':\n    result = a / b if b != 0 else 'Ошибка!'\n\nprint(f'Результат: {result}')\n\n🎯 Задание: добавьте операции // и **"
            ),
            "Условия и циклы" to listOf(
                "Условный оператор if" to "🔀 Условия позволяют выполнять код в зависимости от ситуации.\n\n📝 Синтаксис:\nif условие:\n    # код если True\nelif другое_условие:\n    # альтернатива\nelse:\n    # если ничего не подошло\n\n💡 Пример:\nage = 18\nif age >= 18:\n    print('Совершеннолетний')\nelse:\n    print('Несовершеннолетний')\n\n⚠️ Важно: отступы в Python обязательны!",
                "Логические выражения" to "🔗 Логические операторы объединяют условия.\n\n📌 and — оба условия True:\nif age >= 18 and has_passport:\n    print('Можно лететь')\n\n📌 or — хотя бы одно True:\nif is_student or is_pensioner:\n    print('Скидка!')\n\n📌 not — инверсия:\nif not is_banned:\n    print('Доступ разрешён')\n\n🎯 Приоритет: not → and → or\n\n💡 Используйте скобки для ясности:\nif (a > 5) and (b < 10):",
                "Цикл for" to "🔄 for — цикл для перебора элементов.\n\n📝 Синтаксис:\nfor элемент in последовательность:\n    # действие\n\n💡 Примеры:\n# Перебор списка\nfor fruit in ['яблоко', 'банан']:\n    print(fruit)\n\n# range() — диапазон чисел\nfor i in range(5):  # 0,1,2,3,4\n    print(i)\n\nfor i in range(1, 10, 2):  # 1,3,5,7,9\n    print(i)\n\n🔢 enumerate() — с индексами:\nfor i, item in enumerate(список):\n    print(f'{i}: {item}')",
                "Цикл while" to "🔁 while — цикл с условием.\n\n📝 Синтаксис:\nwhile условие:\n    # код пока условие True\n\n💡 Пример:\ncount = 0\nwhile count < 5:\n    print(count)\n    count += 1\n\n⚡ Управление циклом:\n• break — выход из цикла\n• continue — пропуск итерации\n\nwhile True:\n    cmd = input('Команда: ')\n    if cmd == 'exit':\n        break\n    if cmd == 'skip':\n        continue\n    print(f'Выполняю: {cmd}')\n\n⚠️ Избегайте бесконечных циклов!",
                "Практика: Угадай число" to "🎮 Создаём игру 'Угадай число'!\n\n📝 Код:\nimport random\n\nsecret = random.randint(1, 100)\nattempts = 0\n\nprint('Я загадал число от 1 до 100!')\n\nwhile True:\n    guess = int(input('Ваша догадка: '))\n    attempts += 1\n    \n    if guess < secret:\n        print('Больше!')\n    elif guess > secret:\n        print('Меньше!')\n    else:\n        print(f'Верно! Попыток: {attempts}')\n        break\n\n🎯 Задания:\n• Ограничьте попытки (10 макс)\n• Добавьте подсказки 'горячо/холодно'"
            ),
            "Строки и списки" to listOf(
                "Работа со строками" to "📝 Строки — последовательности символов.\n\n🔢 Индексация:\ntext = 'Python'\ntext[0]   # 'P' (первый)\ntext[-1]  # 'n' (последний)\n\n✂️ Срезы [start:end:step]:\ntext[0:3]   # 'Pyt'\ntext[::2]   # 'Pto'\ntext[::-1]  # 'nohtyP' (реверс)\n\n🛠 Методы:\n'hello'.upper()      # 'HELLO'\n'HELLO'.lower()      # 'hello'\n'a,b,c'.split(',')   # ['a','b','c']\n' hi '.strip()       # 'hi'",
                "Форматирование строк" to "✨ Способы форматирования:\n\n1️⃣ f-строки (рекомендуется):\nname = 'Анна'\nage = 25\nf'{name}, {age} лет'\n\n2️⃣ format():\n'{} + {} = {}'.format(2, 3, 5)\n'{name} - {job}'.format(name='Иван', job='dev')\n\n3️⃣ Конкатенация:\n'Привет, ' + name + '!'\n\n🎨 Форматирование чисел:\nf'{3.14159:.2f}'  # '3.14'\nf'{1000:,}'       # '1,000'\nf'{42:05d}'       # '00042'",
                "Списки" to "📋 Списки — изменяемые коллекции.\n\n📝 Создание:\nnums = [1, 2, 3]\nmixed = [1, 'два', True]\nempty = []\n\n🔢 Индексация:\nnums[0]    # 1\nnums[-1]   # 3\nnums[1:3]  # [2, 3]\n\n✏️ Изменение:\nnums[0] = 10  # [10, 2, 3]\n\n📏 Длина:\nlen(nums)  # 3\n\n🔍 Проверка:\n2 in nums  # True\n5 in nums  # False",
                "Методы списков" to "🛠 Основные методы:\n\n➕ Добавление:\nlst.append(x)     # в конец\nlst.insert(i, x)  # по индексу\nlst.extend([1,2]) # несколько\n\n➖ Удаление:\nlst.remove(x)  # по значению\nlst.pop()      # последний\nlst.pop(i)     # по индексу\nlst.clear()    # всё\n\n🔄 Сортировка:\nlst.sort()           # по возрастанию\nlst.sort(reverse=True)  # убывание\nlst.reverse()        # реверс\n\n🔍 Поиск:\nlst.index(x)  # индекс элемента\nlst.count(x)  # количество",
                "Практика: Список задач" to "📝 Создаём TODO-приложение!\n\n📝 Код:\ntasks = []\n\nwhile True:\n    print('\\n1-Добавить 2-Показать 3-Удалить 4-Выход')\n    choice = input('Выбор: ')\n    \n    if choice == '1':\n        task = input('Задача: ')\n        tasks.append(task)\n        print('✅ Добавлено!')\n    elif choice == '2':\n        for i, t in enumerate(tasks, 1):\n            print(f'{i}. {t}')\n    elif choice == '3':\n        idx = int(input('Номер: ')) - 1\n        tasks.pop(idx)\n    elif choice == '4':\n        break\n\n🎯 Добавьте: отметку выполнения, сохранение в файл"
            ),
            "Функции" to listOf(
                "Создание функций" to "🔧 Функции — переиспользуемые блоки кода.\n\n📝 Синтаксис:\ndef имя_функции(параметры):\n    '''Документация'''\n    # код\n    return результат\n\n💡 Пример:\ndef greet(name):\n    '''Приветствует пользователя'''\n    return f'Привет, {name}!'\n\nprint(greet('Анна'))  # Привет, Анна!\n\n📌 Вызов функции:\nresult = greet('Иван')\n\n⚠️ Функция без return возвращает None",
                "Возврат значений" to "↩️ return — возврат значения из функции.\n\n📝 Один результат:\ndef square(x):\n    return x ** 2\n\n📦 Несколько значений (кортеж):\ndef min_max(numbers):\n    return min(numbers), max(numbers)\n\nlo, hi = min_max([3, 1, 4, 1, 5])\n# lo=1, hi=5\n\n⚡ Ранний выход:\ndef divide(a, b):\n    if b == 0:\n        return None  # выход\n    return a / b\n\n💡 Можно возвращать любой тип: числа, строки, списки, словари...",
                "Аргументы по умолчанию" to "⚙️ Значения по умолчанию:\ndef greet(name, greeting='Привет'):\n    return f'{greeting}, {name}!'\n\ngreet('Анна')           # Привет, Анна!\ngreet('Анна', 'Здравствуй')  # Здравствуй, Анна!\n\n📌 Именованные аргументы:\ndef info(name, age, city):\n    print(f'{name}, {age}, {city}')\n\ninfo(age=25, name='Иван', city='Москва')\n\n⚡ *args — произвольное число аргументов:\ndef sum_all(*nums):\n    return sum(nums)\n\n📦 **kwargs — именованные:\ndef config(**opts):\n    print(opts)",
                "Область видимости" to "🔍 Область видимости переменных:\n\n📌 Локальные — внутри функции:\ndef func():\n    x = 10  # локальная\n    print(x)\n\n📌 Глобальные — вне функций:\ncount = 0\n\ndef increment():\n    global count  # используем глобальную\n    count += 1\n\n⚠️ Без global создаётся локальная!\n\n🔄 nonlocal — для вложенных функций:\ndef outer():\n    x = 1\n    def inner():\n        nonlocal x\n        x += 1\n    inner()\n    print(x)  # 2",
                "Практика: Библиотека функций" to "📚 Создаём полезные функции!\n\n📝 Код:\ndef is_palindrome(text):\n    '''Проверка палиндрома'''\n    clean = text.lower().replace(' ', '')\n    return clean == clean[::-1]\n\ndef factorial(n):\n    '''Факториал числа'''\n    if n <= 1:\n        return 1\n    return n * factorial(n - 1)\n\ndef fibonacci(n):\n    '''N чисел Фибоначчи'''\n    fib = [0, 1]\n    for i in range(2, n):\n        fib.append(fib[-1] + fib[-2])\n    return fib[:n]\n\n# Тесты\nprint(is_palindrome('А роза упала на лапу Азора'))\nprint(factorial(5))  # 120\nprint(fibonacci(10))"
            ),
            "ООП в Python" to listOf(
                "Классы и объекты" to "🏗 ООП — парадигма программирования.\n\n📝 Создание класса:\nclass Dog:\n    species = 'Canis'  # атрибут класса\n    \n    def bark(self):  # метод\n        print('Гав!')\n\n🐕 Создание объекта:\nmy_dog = Dog()\nmy_dog.bark()  # Гав!\n\n📌 self — ссылка на текущий объект\n\n💡 Класс = чертёж\nОбъект = конкретный экземпляр",
                "Конструктор __init__" to "🔨 __init__ — конструктор класса.\n\n📝 Инициализация:\nclass Person:\n    def __init__(self, name, age):\n        self.name = name  # атрибут экземпляра\n        self.age = age\n    \n    def info(self):\n        return f'{self.name}, {self.age} лет'\n\n👤 Создание:\nperson = Person('Анна', 25)\nprint(person.info())  # Анна, 25 лет\n\n⚠️ __init__ вызывается автоматически при создании объекта",
                "Наследование" to "👨‍👦 Наследование — создание подклассов.\n\n📝 Синтаксис:\nclass Animal:\n    def __init__(self, name):\n        self.name = name\n    def speak(self):\n        pass\n\nclass Cat(Animal):  # наследует Animal\n    def speak(self):\n        return 'Мяу!'\n\nclass Dog(Animal):\n    def speak(self):\n        return 'Гав!'\n\n🔄 super() — вызов родителя:\nclass Kitten(Cat):\n    def __init__(self, name, color):\n        super().__init__(name)\n        self.color = color",
                "Инкапсуляция" to "🔒 Инкапсуляция — скрытие данных.\n\n📌 Соглашения:\n_name   — 'защищённый' (не трогать)\n__name  — 'приватный' (name mangling)\n\n📝 @property — геттеры/сеттеры:\nclass Account:\n    def __init__(self):\n        self._balance = 0\n    \n    @property\n    def balance(self):\n        return self._balance\n    \n    @balance.setter\n    def balance(self, value):\n        if value >= 0:\n            self._balance = value\n\nacc = Account()\nacc.balance = 100\nprint(acc.balance)  # 100",
                "Практика: Игровые персонажи" to "🎮 Создаём RPG персонажей!\n\n📝 Код:\nclass Character:\n    def __init__(self, name, hp=100):\n        self.name = name\n        self.hp = hp\n    \n    def attack(self, target):\n        damage = 10\n        target.hp -= damage\n        print(f'{self.name} атакует {target.name}!')\n\nclass Warrior(Character):\n    def __init__(self, name):\n        super().__init__(name, hp=150)\n    \n    def attack(self, target):\n        damage = 20\n        target.hp -= damage\n        print(f'{self.name} мощно бьёт!')\n\nclass Mage(Character):\n    def fireball(self, target):\n        target.hp -= 30\n        print(f'{self.name} кастует огненный шар!')\n\n# Бой\nwarrior = Warrior('Конан')\nmage = Mage('Гендальф')\nwarrior.attack(mage)"
            ),
            "Работа с файлами" to listOf(
                "Открытие файлов" to "📂 open() — открытие файлов.\n\n📝 Синтаксис:\nfile = open('file.txt', 'r')\n\n🔑 Режимы:\n'r'  — чтение (по умолчанию)\n'w'  — запись (перезапись)\n'a'  — добавление в конец\n'x'  — создание нового\n'b'  — бинарный режим\n'r+' — чтение и запись\n\n💡 Пример:\nf = open('data.txt', 'r', encoding='utf-8')\ncontent = f.read()\nf.close()  # Важно закрыть!\n\n⚠️ Всегда указывайте encoding='utf-8'",
                "Чтение файлов" to "📖 Методы чтения:\n\n📝 read() — весь файл:\nwith open('file.txt') as f:\n    content = f.read()\n\n📝 readline() — одна строка:\nline = f.readline()\n\n📝 readlines() — список строк:\nlines = f.readlines()\n\n🔄 Итерация по строкам:\nwith open('file.txt') as f:\n    for line in f:\n        print(line.strip())\n\n💡 strip() убирает \\n в конце",
                "Запись в файлы" to "✏️ Методы записи:\n\n📝 write() — строка:\nwith open('file.txt', 'w') as f:\n    f.write('Привет!\\n')\n    f.write('Мир!')\n\n📝 writelines() — список:\nlines = ['Строка 1\\n', 'Строка 2\\n']\nwith open('file.txt', 'w') as f:\n    f.writelines(lines)\n\n➕ Добавление (mode='a'):\nwith open('log.txt', 'a') as f:\n    f.write('Новая запись\\n')\n\n⚠️ 'w' перезаписывает файл!",
                "Контекстный менеджер" to "🔐 with — автоматическое закрытие.\n\n📝 Синтаксис:\nwith open('file.txt') as f:\n    content = f.read()\n# файл закрыт автоматически!\n\n✅ Преимущества:\n• Автозакрытие даже при ошибке\n• Чистый код\n• Нет утечек ресурсов\n\n📦 Несколько файлов:\nwith open('in.txt') as src, open('out.txt', 'w') as dst:\n    dst.write(src.read())\n\n💡 Всегда используйте with для файлов!",
                "Практика: Блокнот" to "📓 Создаём блокнот!\n\n📝 Код:\nimport os\n\nFILE = 'notes.txt'\n\ndef save_note(text):\n    with open(FILE, 'a', encoding='utf-8') as f:\n        f.write(text + '\\n')\n\ndef show_notes():\n    if not os.path.exists(FILE):\n        print('Заметок нет')\n        return\n    with open(FILE, encoding='utf-8') as f:\n        for i, line in enumerate(f, 1):\n            print(f'{i}. {line.strip()}')\n\nwhile True:\n    cmd = input('\\n(s)ave/(l)ist/(q)uit: ')\n    if cmd == 's':\n        note = input('Заметка: ')\n        save_note(note)\n    elif cmd == 'l':\n        show_notes()\n    elif cmd == 'q':\n        break"
            ),
            "Модули и пакеты" to listOf(
                "Импорт модулей" to "📦 Импорт — подключение кода.\n\n📝 Способы:\nimport math\nmath.sqrt(16)  # 4.0\n\nfrom math import sqrt, pi\nsqrt(16)  # 4.0\n\nfrom math import *  # всё (не рекомендуется)\n\nimport numpy as np  # псевдоним\n\n💡 Стандартные модули:\n• os — работа с ОС\n• sys — системные функции\n• datetime — дата/время\n• random — случайные числа\n• json — работа с JSON",
                "Стандартная библиотека" to "📚 Полезные модули Python:\n\n🗂 os — работа с файловой системой:\nimport os\nos.getcwd()      # текущая папка\nos.listdir('.')  # файлы в папке\nos.path.exists('file.txt')\n\n📅 datetime — дата и время:\nfrom datetime import datetime\nnow = datetime.now()\nprint(now.strftime('%d.%m.%Y'))\n\n🎲 random — случайные числа:\nimport random\nrandom.randint(1, 100)\nrandom.choice(['a', 'b', 'c'])\n\n🔢 math — математика:\nimport math\nmath.sqrt(16), math.pi",
                "Создание модулей" to "🔧 Создание своего модуля:\n\n📝 mymodule.py:\ndef greet(name):\n    return f'Привет, {name}!'\n\nPI = 3.14159\n\nif __name__ == '__main__':\n    # Код для тестирования\n    print(greet('Тест'))\n\n📦 Использование:\nfrom mymodule import greet, PI\nprint(greet('Мир'))\n\n💡 __name__ == '__main__':\n• При импорте: __name__ = 'mymodule'\n• При запуске: __name__ = '__main__'\n\n⚠️ Это позволяет тестировать модуль отдельно",
                "Пакеты" to "📁 Пакет — папка с модулями.\n\n📂 Структура:\nmy_package/\n├── __init__.py\n├── module1.py\n└── module2.py\n\n📝 __init__.py (может быть пустым):\nfrom .module1 import func1\nfrom .module2 import func2\n\n📦 Использование:\nfrom my_package import func1\nfrom my_package.module2 import func2\n\n💡 pip — менеджер пакетов:\npip install requests\npip list\npip freeze > requirements.txt",
                "Практика: Свой пакет" to "📦 Создаём пакет утилит!\n\n📂 Структура:\nutils/\n├── __init__.py\n├── strings.py\n└── numbers.py\n\n📝 strings.py:\ndef reverse(s):\n    return s[::-1]\n\ndef is_email(s):\n    return '@' in s and '.' in s\n\n📝 numbers.py:\ndef is_prime(n):\n    if n < 2: return False\n    for i in range(2, int(n**0.5)+1):\n        if n % i == 0: return False\n    return True\n\n📝 __init__.py:\nfrom .strings import reverse, is_email\nfrom .numbers import is_prime\n\n# Использование:\nfrom utils import reverse, is_prime"
            ),
            "Обработка ошибок" to listOf(
                "Типы исключений" to "⚠️ Исключения — ошибки во время выполнения.\n\n📌 Частые типы:\n• ValueError — неверное значение\n  int('abc')  # ValueError\n\n• TypeError — неверный тип\n  'a' + 1  # TypeError\n\n• IndexError — индекс за пределами\n  [1,2][5]  # IndexError\n\n• KeyError — ключ не найден\n  {}['key']  # KeyError\n\n• FileNotFoundError — файл не найден\n• ZeroDivisionError — деление на 0\n\n💡 Все наследуют от Exception",
                "Try/except" to "🛡 try/except — перехват ошибок.\n\n📝 Синтаксис:\ntry:\n    result = 10 / 0\nexcept ZeroDivisionError:\n    print('Деление на ноль!')\n\n📦 Несколько исключений:\ntry:\n    x = int(input())\nexcept ValueError:\n    print('Не число!')\nexcept Exception as e:\n    print(f'Ошибка: {e}')\n\n✅ else — если ошибок нет:\ntry:\n    f = open('file.txt')\nexcept:\n    print('Ошибка')\nelse:\n    print(f.read())\nfinally:\n    print('Всегда выполнится')",
                "Raise" to "🚨 raise — генерация исключений.\n\n📝 Синтаксис:\ndef divide(a, b):\n    if b == 0:\n        raise ValueError('Делитель не может быть 0!')\n    return a / b\n\n🔧 Свои исключения:\nclass AgeError(Exception):\n    pass\n\ndef set_age(age):\n    if age < 0:\n        raise AgeError('Возраст не может быть отрицательным')\n    return age\n\ntry:\n    set_age(-5)\nexcept AgeError as e:\n    print(e)",
                "Отладка" to "🔍 Методы отладки:\n\n📝 print() — простейший способ:\ndef calc(x):\n    print(f'DEBUG: x = {x}')\n    return x * 2\n\n✅ assert — проверка условий:\ndef divide(a, b):\n    assert b != 0, 'Делитель = 0!'\n    return a / b\n\n🐛 pdb — интерактивный отладчик:\nimport pdb\n\ndef buggy():\n    x = 1\n    pdb.set_trace()  # точка останова\n    y = x + 'error'\n\n💡 Команды pdb:\nn — следующая строка\nc — продолжить\np x — вывести x\nq — выход",
                "Практика: Надёжный код" to "🛡 Делаем код надёжным!\n\n📝 До (хрупкий код):\ndata = input('Число: ')\nresult = 100 / int(data)\nprint(result)\n\n✅ После (надёжный):\ndef safe_divide():\n    try:\n        data = input('Число: ')\n        num = int(data)\n        if num == 0:\n            raise ValueError('Ноль недопустим')\n        return 100 / num\n    except ValueError as e:\n        print(f'Ошибка ввода: {e}')\n        return None\n    except Exception as e:\n        print(f'Неизвестная ошибка: {e}')\n        return None\n\nresult = safe_divide()\nif result:\n    print(f'Результат: {result}')"
            ),
            "Декораторы" to listOf(
                "Функции высшего порядка" to "🔄 Функции как объекты первого класса.\n\n📝 Функция как аргумент:\ndef apply(func, value):\n    return func(value)\n\ndef double(x):\n    return x * 2\n\nresult = apply(double, 5)  # 10\n\n📦 Функция как результат:\ndef multiplier(n):\n    def multiply(x):\n        return x * n\n    return multiply\n\ndouble = multiplier(2)\ntriple = multiplier(3)\nprint(double(5))  # 10\nprint(triple(5))  # 15\n\n💡 Это основа для декораторов!",
                "Создание декораторов" to "🎀 Декоратор — обёртка для функции.\n\n📝 Синтаксис:\ndef my_decorator(func):\n    def wrapper(*args, **kwargs):\n        print('До вызова')\n        result = func(*args, **kwargs)\n        print('После вызова')\n        return result\n    return wrapper\n\n@my_decorator\ndef greet(name):\n    print(f'Привет, {name}!')\n\ngreet('Мир')\n# До вызова\n# Привет, Мир!\n# После вызова\n\n💡 @decorator = func = decorator(func)",
                "Декораторы с аргументами" to "⚙️ Параметризованные декораторы:\n\n📝 Синтаксис (3 уровня вложенности):\ndef repeat(times):\n    def decorator(func):\n        def wrapper(*args, **kwargs):\n            for _ in range(times):\n                result = func(*args, **kwargs)\n            return result\n        return wrapper\n    return decorator\n\n@repeat(3)\ndef say_hi():\n    print('Привет!')\n\nsay_hi()\n# Привет!\n# Привет!\n# Привет!\n\n💡 @repeat(3) = decorator = repeat(3)",
                "Встроенные декораторы" to "🔧 Встроенные декораторы Python:\n\n📌 @staticmethod — без self:\nclass Math:\n    @staticmethod\n    def add(a, b):\n        return a + b\n\nMath.add(2, 3)  # 5\n\n📌 @classmethod — с cls:\nclass User:\n    count = 0\n    @classmethod\n    def get_count(cls):\n        return cls.count\n\n📌 @property — геттер:\nclass Circle:\n    def __init__(self, r):\n        self._r = r\n    \n    @property\n    def area(self):\n        return 3.14 * self._r ** 2\n\nc = Circle(5)\nprint(c.area)  # 78.5",
                "Практика: Логирование" to "📋 Создаём декоратор логирования!\n\n📝 Код:\nimport time\nfrom functools import wraps\n\ndef log_call(func):\n    @wraps(func)  # сохраняет имя функции\n    def wrapper(*args, **kwargs):\n        start = time.time()\n        print(f'▶ {func.__name__}({args}, {kwargs})')\n        \n        result = func(*args, **kwargs)\n        \n        elapsed = time.time() - start\n        print(f'◀ {func.__name__} = {result} [{elapsed:.3f}s]')\n        return result\n    return wrapper\n\n@log_call\ndef slow_add(a, b):\n    time.sleep(0.5)\n    return a + b\n\nslow_add(2, 3)\n# ▶ slow_add((2, 3), {})\n# ◀ slow_add = 5 [0.501s]"
            ),
            "Генераторы" to listOf(
                "Итераторы" to "🔄 Итератор — объект для перебора.\n\n📝 Протокол итератора:\nclass Counter:\n    def __init__(self, max):\n        self.max = max\n        self.n = 0\n    \n    def __iter__(self):\n        return self\n    \n    def __next__(self):\n        if self.n >= self.max:\n            raise StopIteration\n        self.n += 1\n        return self.n\n\nfor i in Counter(3):\n    print(i)  # 1, 2, 3\n\n💡 iter() и next():\nlst = [1, 2, 3]\nit = iter(lst)\nnext(it)  # 1",
                "Генераторы" to "⚡ Генератор — ленивый итератор.\n\n📝 yield вместо return:\ndef countdown(n):\n    while n > 0:\n        yield n\n        n -= 1\n\nfor i in countdown(3):\n    print(i)  # 3, 2, 1\n\n💡 Преимущества:\n• Экономия памяти\n• Ленивые вычисления\n• Бесконечные последовательности\n\ndef infinite():\n    n = 0\n    while True:\n        yield n\n        n += 1\n\n⚠️ Генератор можно пройти только раз!",
                "Генераторные выражения" to "📝 Генераторные выражения:\n\n🔄 Синтаксис:\ngen = (x**2 for x in range(10))\n\n💡 vs List comprehension:\n# Список — сразу в памяти\nlst = [x**2 for x in range(1000000)]\n\n# Генератор — по требованию\ngen = (x**2 for x in range(1000000))\n\n📊 Сравнение памяти:\nimport sys\nlst = [i for i in range(1000)]\ngen = (i for i in range(1000))\nprint(sys.getsizeof(lst))  # ~8000 bytes\nprint(sys.getsizeof(gen))  # ~120 bytes\n\n⚡ Используйте генераторы для больших данных!",
                "itertools" to "🔧 itertools — мощные итераторы.\n\n📦 Бесконечные:\nfrom itertools import count, cycle, repeat\ncount(10)      # 10, 11, 12...\ncycle('AB')    # A, B, A, B...\nrepeat('X', 3) # X, X, X\n\n🔗 Комбинаторика:\nfrom itertools import permutations, combinations\nlist(permutations('AB'))  # [('A','B'), ('B','A')]\nlist(combinations('ABC', 2))  # [('A','B'), ('A','C'), ('B','C')]\n\n⚡ Полезные:\nfrom itertools import chain, islice\nchain([1,2], [3,4])  # 1,2,3,4\nislice(count(), 5)   # 0,1,2,3,4",
                "Практика: Обработка данных" to "📊 Обработка большого файла!\n\n📝 Код:\ndef read_large_file(path):\n    '''Читает файл построчно'''\n    with open(path) as f:\n        for line in f:\n            yield line.strip()\n\ndef filter_lines(lines, keyword):\n    '''Фильтрует строки'''\n    for line in lines:\n        if keyword in line:\n            yield line\n\ndef process_data(path, keyword):\n    lines = read_large_file(path)\n    filtered = filter_lines(lines, keyword)\n    \n    for i, line in enumerate(filtered):\n        print(f'{i+1}: {line}')\n        if i >= 9:  # первые 10\n            break\n\n# Обработка файла любого размера!\nprocess_data('huge_log.txt', 'ERROR')"
            ),
            "Асинхронность" to listOf(
                "Введение в async" to "⚡ Асинхронность — параллельное выполнение.\n\n🔄 Синхронный код:\nimport time\ndef task():\n    time.sleep(1)\n    print('Готово')\n\ntask()  # 1 сек\ntask()  # ещё 1 сек\n# Итого: 2 сек\n\n⚡ Асинхронный код:\nimport asyncio\nasync def task():\n    await asyncio.sleep(1)\n    print('Готово')\n\nasync def main():\n    await asyncio.gather(task(), task())\n\nasyncio.run(main())\n# Итого: 1 сек!\n\n💡 Event Loop управляет задачами",
                "async/await" to "🔑 async/await — ключевые слова.\n\n📝 async — объявление корутины:\nasync def fetch_data():\n    # асинхронный код\n    return data\n\n📝 await — ожидание результата:\nasync def main():\n    result = await fetch_data()\n    print(result)\n\n⚠️ await только внутри async!\n\n💡 Корутина vs функция:\ndef sync_func():    # обычная\n    return 1\n\nasync def async_func():  # корутина\n    return 1\n\n# Вызов:\nsync_func()           # 1\nawait async_func()    # 1",
                "asyncio" to "📦 asyncio — модуль асинхронности.\n\n🚀 Запуск:\nimport asyncio\n\nasync def main():\n    print('Старт')\n    await asyncio.sleep(1)\n    print('Финиш')\n\nasyncio.run(main())\n\n📋 Создание задач:\nasync def main():\n    task1 = asyncio.create_task(fetch(url1))\n    task2 = asyncio.create_task(fetch(url2))\n    \n    result1 = await task1\n    result2 = await task2\n\n⏱ Таймаут:\ntry:\n    await asyncio.wait_for(task, timeout=5.0)\nexcept asyncio.TimeoutError:\n    print('Таймаут!')",
                "Параллельное выполнение" to "🔀 Параллельный запуск задач:\n\n📦 gather — все сразу:\nresults = await asyncio.gather(\n    fetch(url1),\n    fetch(url2),\n    fetch(url3)\n)\n\n📦 wait — с контролем:\ndone, pending = await asyncio.wait(\n    tasks,\n    return_when=asyncio.FIRST_COMPLETED\n)\n\n🚦 Семафор — ограничение:\nsem = asyncio.Semaphore(10)  # макс 10\n\nasync def limited_fetch(url):\n    async with sem:\n        return await fetch(url)\n\n💡 Используйте для ограничения нагрузки на сервер",
                "Практика: Асинхронный парсер" to "🌐 Асинхронный парсер сайтов!\n\n📝 Код:\nimport asyncio\nimport aiohttp\n\nasync def fetch(session, url):\n    async with session.get(url) as resp:\n        return await resp.text()\n\nasync def parse_urls(urls):\n    async with aiohttp.ClientSession() as session:\n        tasks = [fetch(session, url) for url in urls]\n        results = await asyncio.gather(*tasks)\n        return results\n\nurls = [\n    'https://example.com',\n    'https://python.org',\n    'https://github.com'\n]\n\nresults = asyncio.run(parse_urls(urls))\nfor url, html in zip(urls, results):\n    print(f'{url}: {len(html)} символов')\n\n💡 pip install aiohttp"
            ),
            "Работа с API" to listOf(
                "HTTP протокол" to "🌐 HTTP — протокол веба.\n\n📝 Методы:\n• GET — получить данные\n• POST — отправить данные\n• PUT — обновить\n• DELETE — удалить\n\n📋 Заголовки:\nContent-Type: application/json\nAuthorization: Bearer token123\n\n📊 Коды ответов:\n• 200 OK — успех\n• 201 Created — создано\n• 400 Bad Request — ошибка клиента\n• 401 Unauthorized — не авторизован\n• 404 Not Found — не найдено\n• 500 Server Error — ошибка сервера",
                "Библиотека requests" to "📦 requests — HTTP для людей.\n\n🔧 Установка:\npip install requests\n\n📝 GET запрос:\nimport requests\n\nresp = requests.get('https://api.github.com')\nprint(resp.status_code)  # 200\nprint(resp.json())       # данные\n\n📝 POST запрос:\ndata = {'name': 'Иван', 'age': 25}\nresp = requests.post(url, json=data)\n\n📋 Заголовки:\nheaders = {'Authorization': 'Bearer token'}\nresp = requests.get(url, headers=headers)\n\n⏱ Таймаут:\nresp = requests.get(url, timeout=5)",
                "JSON" to "📦 JSON — формат обмена данными.\n\n📝 Python → JSON:\nimport json\n\ndata = {'name': 'Анна', 'age': 25}\njson_str = json.dumps(data, ensure_ascii=False)\n# '{\"name\": \"Анна\", \"age\": 25}'\n\n📝 JSON → Python:\njson_str = '{\"name\": \"Иван\"}'\ndata = json.loads(json_str)\nprint(data['name'])  # Иван\n\n📁 Работа с файлами:\n# Запись\nwith open('data.json', 'w') as f:\n    json.dump(data, f)\n\n# Чтение\nwith open('data.json') as f:\n    data = json.load(f)",
                "REST API" to "🔗 REST — архитектура API.\n\n📌 Принципы:\n• Ресурсы: /users, /posts\n• Методы: GET, POST, PUT, DELETE\n• Stateless — без состояния\n\n📝 Примеры:\nGET /users        — все пользователи\nGET /users/1      — пользователь #1\nPOST /users       — создать\nPUT /users/1      — обновить #1\nDELETE /users/1   — удалить #1\n\n💡 Работа с API:\nimport requests\n\n# Получить пользователей\nusers = requests.get('https://api.example.com/users').json()\n\n# Создать пользователя\nnew_user = {'name': 'Иван'}\nresp = requests.post(url, json=new_user)",
                "Практика: Погодное приложение" to "🌤 Создаём погодное приложение!\n\n📝 Код:\nimport requests\n\nAPI_KEY = 'your_api_key'  # openweathermap.org\nBASE_URL = 'https://api.openweathermap.org/data/2.5/weather'\n\ndef get_weather(city):\n    params = {\n        'q': city,\n        'appid': API_KEY,\n        'units': 'metric',\n        'lang': 'ru'\n    }\n    resp = requests.get(BASE_URL, params=params)\n    \n    if resp.status_code == 200:\n        data = resp.json()\n        return {\n            'city': data['name'],\n            'temp': data['main']['temp'],\n            'desc': data['weather'][0]['description']\n        }\n    return None\n\nweather = get_weather('Москва')\nif weather:\n    print(f\"{weather['city']}: {weather['temp']}°C, {weather['desc']}\")"
            )
        )

        allCourses.forEachIndexed { index, (title, description, level) ->
            val courseId = index + 1
            db.execSQL(
                "INSERT INTO courses (id, title, description, level, icon, lessons_count) VALUES (?, ?, ?, ?, '🐍', 5)",
                arrayOf(courseId, title, description, level)
            )

            lessonsData[title]?.forEachIndexed { lessonIndex, (lessonTitle, lessonContent) ->
                db.execSQL(
                    "INSERT INTO lessons (course_id, title, content, order_num, duration_minutes) VALUES (?, ?, ?, ?, ?)",
                    arrayOf(courseId, lessonTitle, lessonContent, lessonIndex + 1, 10 + (lessonIndex * 2))
                )
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS lessons")
            db.execSQL("DROP TABLE IF EXISTS user_lesson_progress")
            db.execSQL("DROP TABLE IF EXISTS courses")
            db.execSQL("DROP TABLE IF EXISTS user_stats")

            db.execSQL(
                """
                CREATE TABLE courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    level TEXT NOT NULL,
                    icon TEXT DEFAULT '🐍',
                    lessons_count INTEGER DEFAULT 5
                );
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE lessons (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    order_num INTEGER NOT NULL,
                    duration_minutes INTEGER DEFAULT 10,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
                );
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS user_stats (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_login TEXT NOT NULL UNIQUE,
                    completed_courses INTEGER NOT NULL DEFAULT 0,
                    total_time_minutes INTEGER NOT NULL DEFAULT 0
                );
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE user_lesson_progress (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_login TEXT NOT NULL,
                    lesson_id INTEGER NOT NULL,
                    completed INTEGER DEFAULT 0,
                    completed_at TEXT,
                    UNIQUE(user_login, lesson_id)
                );
                """.trimIndent()
            )

            insertInitialData(db)
        }

        if (oldVersion < 4) {
            // Добавляем колонку avatar в таблицу users
            try {
                db.execSQL("ALTER TABLE users ADD COLUMN avatar TEXT DEFAULT '🐍'")
            } catch (e: Exception) {
                // Колонка уже существует
            }
        }

        if (oldVersion < 5) {
            // Добавляем таблицу избранного
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS favorites (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_login TEXT NOT NULL,
                    course_id INTEGER NOT NULL,
                    UNIQUE(user_login, course_id)
                );
                """.trimIndent()
            )
        }

        if (oldVersion < 6) {
            // Добавляем таблицу сообщений поддержки
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS support_messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_login TEXT NOT NULL,
                    message TEXT NOT NULL,
                    is_from_admin INTEGER DEFAULT 0,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                );
                """.trimIndent()
            )
        }

        if (oldVersion < 7) {
            // Добавляем поле секретного слова для восстановления пароля
            try {
                db.execSQL("ALTER TABLE users ADD COLUMN secret_word TEXT DEFAULT ''")
            } catch (e: Exception) {
                // Колонка уже существует
            }
        }

        if (oldVersion < 8) {
            // Добавляем таблицу вопросов тестов
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS test_questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    question_text TEXT NOT NULL,
                    option1 TEXT NOT NULL,
                    option2 TEXT NOT NULL,
                    option3 TEXT NOT NULL,
                    option4 TEXT NOT NULL,
                    correct_option INTEGER NOT NULL,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
                );
                """.trimIndent()
            )
        }
    }

    // Методы для избранного
    fun addToFavorites(userLogin: String, courseId: Int): Boolean {
        return try {
            writableDatabase.execSQL(
                "INSERT OR IGNORE INTO favorites (user_login, course_id) VALUES (?, ?)",
                arrayOf(userLogin, courseId)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeFromFavorites(userLogin: String, courseId: Int): Boolean {
        return try {
            writableDatabase.delete(
                "favorites",
                "user_login = ? AND course_id = ?",
                arrayOf(userLogin, courseId.toString())
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isFavorite(userLogin: String, courseId: Int): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT id FROM favorites WHERE user_login = ? AND course_id = ?",
            arrayOf(userLogin, courseId.toString())
        )
        return cursor.use { it.moveToFirst() }
    }

    fun getFavoriteCourses(userLogin: String): List<Course> {
        val courses = mutableListOf<Course>()
        val cursor = readableDatabase.rawQuery(
            """
            SELECT c.id, c.title, c.description, c.level, c.icon, c.lessons_count 
            FROM courses c
            INNER JOIN favorites f ON c.id = f.course_id
            WHERE f.user_login = ?
            ORDER BY c.title
            """.trimIndent(),
            arrayOf(userLogin)
        )
        cursor.use {
            while (it.moveToNext()) {
                courses.add(
                    Course(
                        id = it.getInt(0),
                        title = it.getString(1),
                        description = it.getString(2),
                        level = it.getString(3),
                        icon = it.getString(4) ?: "🐍",
                        lessonsCount = it.getInt(5)
                    )
                )
            }
        }
        return courses
    }

    // CRUD операции для курсов
    fun addCourse(title: String, description: String, level: String): Long {
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("level", level)
            put("icon", "🐍")
            put("lessons_count", 0)
        }
        return writableDatabase.insert("courses", null, values)
    }

    fun updateCourse(id: Int, title: String, description: String, level: String): Int {
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("level", level)
        }
        return writableDatabase.update("courses", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteCourse(id: Int): Int {
        writableDatabase.delete("lessons", "course_id = ?", arrayOf(id.toString()))
        return writableDatabase.delete("courses", "id = ?", arrayOf(id.toString()))
    }

    fun getAllCourses(): List<Course> {
        val courses = mutableListOf<Course>()
        val cursor = readableDatabase.rawQuery(
            "SELECT id, title, description, level, icon, lessons_count FROM courses ORDER BY level, title",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                courses.add(
                    Course(
                        id = it.getInt(0),
                        title = it.getString(1),
                        description = it.getString(2),
                        level = it.getString(3),
                        icon = it.getString(4) ?: "🐍",
                        lessonsCount = it.getInt(5)
                    )
                )
            }
        }
        return courses
    }

    fun getLessonsForCourse(courseId: Int): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        val cursor = readableDatabase.rawQuery(
            "SELECT id, course_id, title, content, order_num, duration_minutes FROM lessons WHERE course_id = ? ORDER BY order_num",
            arrayOf(courseId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                lessons.add(
                    Lesson(
                        id = it.getInt(0),
                        courseId = it.getInt(1),
                        title = it.getString(2),
                        content = it.getString(3),
                        orderNum = it.getInt(4),
                        durationMinutes = it.getInt(5)
                    )
                )
            }
        }
        return lessons
    }

    fun addLesson(courseId: Int, title: String, content: String, orderNum: Int, duration: Int = 10): Long {
        val values = ContentValues().apply {
            put("course_id", courseId)
            put("title", title)
            put("content", content)
            put("order_num", orderNum)
            put("duration_minutes", duration)
        }
        val id = writableDatabase.insert("lessons", null, values)
        updateLessonsCount(courseId)
        return id
    }

    fun updateLesson(lessonId: Int, title: String, content: String, duration: Int): Int {
        val values = ContentValues().apply {
            put("title", title)
            put("content", content)
            put("duration_minutes", duration)
        }
        return writableDatabase.update("lessons", values, "id = ?", arrayOf(lessonId.toString()))
    }

    fun deleteLesson(lessonId: Int): Int {
        // Получаем course_id перед удалением
        var courseId = 0
        val cursor = readableDatabase.rawQuery(
            "SELECT course_id FROM lessons WHERE id = ?",
            arrayOf(lessonId.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                courseId = it.getInt(0)
            }
        }
        val result = writableDatabase.delete("lessons", "id = ?", arrayOf(lessonId.toString()))
        if (courseId > 0) {
            updateLessonsCount(courseId)
        }
        return result
    }

    private fun updateLessonsCount(courseId: Int) {
        writableDatabase.execSQL(
            "UPDATE courses SET lessons_count = (SELECT COUNT(*) FROM lessons WHERE course_id = ?) WHERE id = ?",
            arrayOf(courseId, courseId)
        )
    }

    fun getAllUsersStats(): List<UserStats> {
        val stats = mutableListOf<UserStats>()
        val cursor = readableDatabase.rawQuery(
            """
            SELECT u.login, u.first_name, u.last_name, u.email,
                   COALESCE(s.completed_courses, 0), COALESCE(s.total_time_minutes, 0)
            FROM users u
            LEFT JOIN user_stats s ON u.login = s.user_login
            ORDER BY u.login
            """.trimIndent(),
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                stats.add(
                    UserStats(
                        login = it.getString(0),
                        firstName = it.getString(1),
                        lastName = it.getString(2),
                        email = it.getString(3),
                        completedCourses = it.getInt(4),
                        totalTimeMinutes = it.getInt(5)
                    )
                )
            }
        }
        return stats
    }

    // Методы для прогресса уроков
    fun markLessonCompleted(userLogin: String, lessonId: Int): Boolean {
        return try {
            writableDatabase.execSQL(
                "INSERT OR REPLACE INTO user_lesson_progress (user_login, lesson_id, completed, completed_at) VALUES (?, ?, 1, datetime('now'))",
                arrayOf(userLogin, lessonId)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isLessonCompleted(userLogin: String, lessonId: Int): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT completed FROM user_lesson_progress WHERE user_login = ? AND lesson_id = ?",
            arrayOf(userLogin, lessonId.toString())
        )
        return cursor.use {
            it.moveToFirst() && it.getInt(0) == 1
        }
    }

    fun getCompletedLessonsCount(userLogin: String, courseId: Int): Int {
        val cursor = readableDatabase.rawQuery(
            """
            SELECT COUNT(*) FROM user_lesson_progress ulp
            JOIN lessons l ON ulp.lesson_id = l.id
            WHERE ulp.user_login = ? AND l.course_id = ? AND ulp.completed = 1
            """,
            arrayOf(userLogin, courseId.toString())
        )
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    // Методы для восстановления пароля
    fun verifySecretWord(login: String, secretWord: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT secret_word FROM users WHERE login = ?",
            arrayOf(login)
        )
        return cursor.use {
            if (it.moveToFirst()) {
                val storedWord = it.getString(0) ?: ""
                storedWord.isNotEmpty() && storedWord.equals(secretWord, ignoreCase = true)
            } else false
        }
    }

    fun updatePassword(login: String, newPassword: String): Boolean {
        return try {
            writableDatabase.execSQL(
                "UPDATE users SET password = ? WHERE login = ?",
                arrayOf(newPassword, login)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    // Методы для технической поддержки
    fun sendSupportMessage(userLogin: String, message: String, isFromAdmin: Boolean = false): Boolean {
        return try {
            // Используем московское время (+3 часа от UTC)
            writableDatabase.execSQL(
                "INSERT INTO support_messages (user_login, message, is_from_admin, created_at) VALUES (?, ?, ?, datetime('now', '+3 hours'))",
                arrayOf(userLogin, message, if (isFromAdmin) 1 else 0)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getSupportMessages(userLogin: String): List<SupportMessage> {
        val messages = mutableListOf<SupportMessage>()
        val cursor = readableDatabase.rawQuery(
            "SELECT id, user_login, message, is_from_admin, created_at FROM support_messages WHERE user_login = ? ORDER BY created_at ASC",
            arrayOf(userLogin)
        )
        cursor.use {
            while (it.moveToNext()) {
                messages.add(
                    SupportMessage(
                        id = it.getInt(0),
                        userLogin = it.getString(1),
                        message = it.getString(2),
                        isFromAdmin = it.getInt(3) == 1,
                        createdAt = it.getString(4) ?: ""
                    )
                )
            }
        }
        return messages
    }

    fun getUsersWithMessages(): List<String> {
        val users = mutableListOf<String>()
        val cursor = readableDatabase.rawQuery(
            "SELECT DISTINCT user_login FROM support_messages ORDER BY user_login",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                users.add(it.getString(0))
            }
        }
        return users
    }

    fun getUnreadMessagesCount(userLogin: String): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM support_messages WHERE user_login = ? AND is_from_admin = 0",
            arrayOf(userLogin)
        )
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    // Методы для аватарки из галереи
    fun updateUserAvatar(login: String, avatarPath: String): Boolean {
        return try {
            writableDatabase.execSQL(
                "UPDATE users SET avatar = ? WHERE login = ?",
                arrayOf(avatarPath, login)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserAvatar(login: String): String? {
        val cursor = readableDatabase.rawQuery(
            "SELECT avatar FROM users WHERE login = ?",
            arrayOf(login)
        )
        return cursor.use {
            if (it.moveToFirst()) it.getString(0) else null
        }
    }

    companion object {
        private const val DB_NAME = "pystart_local.db"
        private const val DB_VERSION = 8
    }
}

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val level: String,
    val icon: String = "🐍",
    val lessonsCount: Int = 0
)

data class Lesson(
    val id: Int,
    val courseId: Int,
    val title: String,
    val content: String,
    val orderNum: Int,
    val durationMinutes: Int = 10
)

data class UserStats(
    val login: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val completedCourses: Int,
    val totalTimeMinutes: Int
)

data class SupportMessage(
    val id: Int,
    val userLogin: String,
    val message: String,
    val isFromAdmin: Boolean,
    val createdAt: String
)
