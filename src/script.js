class TypeRacer {
    constructor() {
        // list of common words
        this.words = [
            'the', 'be', 'to', 'of', 'and', 'a', 'in', 'that', 'have', 'I',
            'it', 'for', 'not', 'on', 'with', 'he', 'as', 'you', 'do', 'at',
            'this', 'but', 'his', 'by', 'from', 'they', 'we', 'say', 'her', 'she',
            'computer', 'programming', 'javascript', 'developer', 'application',
            'website', 'software', 'database', 'network', 'interface',
            'algorithm', 'function', 'variable', 'object', 'array',
            'string', 'number', 'boolean', 'method', 'property',
            'about', 'time', 'out', 'if', 'will', 'way', 'my', 'than', 'first',
            'water', 'been', 'call', 'who', 'oil', 'now', 'find', 'long', 'down',
            'day', 'did', 'get', 'come', 'made', 'may', 'part', 'over', 'new',
            'sound', 'take', 'only', 'little', 'work', 'know', 'place', 'year',
            'live', 'back', 'give', 'most', 'very', 'after', 'thing', 'our',
            'just', 'name', 'good', 'sentence', 'man', 'think', 'say', 'great',
            'where', 'help', 'through', 'much', 'before', 'line', 'right', 'too',
            'mean', 'old', 'any', 'same', 'tell', 'boy', 'follow', 'came', 'want'
        ];
        
        // different difficulty settings
        this.difficultySettings = {
            easy: { time: 5, wordCount: 50 },
            medium: { time: 10, wordCount: 75 },
            hard: { time: 20, wordCount: 100 }
        };

        // game state variables
        this.currentWords = [];
        this.typedWords = []; 
        this.currentInput = '';
        this.timeLeft = 0;
        this.timer = null;
        this.correctWords = 0;
        this.totalWordsTyped = 0;
        this.isGameActive = false;
        this.currentDifficulty = null;
        this.startTime = null;

        // grab dom elements
        this.difficultyBtns = document.querySelectorAll('.difficulty-btn');
        this.gameSection = document.querySelector('.game-section');
        this.wordDisplay = document.getElementById('word-display');
        this.wordInput = document.getElementById('word-input');
        this.timeDisplay = document.getElementById('time');
        this.results = document.getElementById('results');
        this.correctWordsDisplay = document.getElementById('correct-words');
        this.accuracyDisplay = document.getElementById('accuracy');
        this.restartBtn = document.getElementById('restart-btn');

        // default difficulty on load
        this.setupGame('medium');

        this.initializeEventListeners();
    }

    initializeEventListeners() {
        // handle difficulty button clicks
        this.difficultyBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const difficulty = btn.dataset.difficulty;
                this.setupGame(difficulty);
            });
        });

        // allow tab key to restart current difficulty
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Tab' && this.currentDifficulty) {
                e.preventDefault();
                this.setupGame(this.currentDifficulty);
            }
        });

        // handle user typing
        this.wordInput.addEventListener('input', (e) => {
            // start game on first character typed
            if (!this.isGameActive && e.target.value.length === 1) {
                this.startGame();
            }
            this.handleInput(e.target.value);
        });

        this.restartBtn.addEventListener('click', () => {
            this.setupGame(this.currentDifficulty);
        });
    }

    handleInput(value) {
        if (!this.isGameActive) return;

        // check if user completed a word (space pressed)
        if (value.endsWith(' ')) {
            const typedWord = value.trim();
            if (typedWord.length > 0) {
                this.checkWord(typedWord);
                this.wordInput.value = '';
                return;
            }
        }

        // update current word being typed
        this.currentInput = value;
        this.updateWordDisplay();
    }

    checkWord(typedWord) {
        if (this.typedWords.length >= this.currentWords.length) return;

        const currentWord = this.currentWords[this.typedWords.length];
        if (typedWord === currentWord) {
            this.correctWords++;
        }
        
        this.totalWordsTyped++;
        this.typedWords.push(typedWord);
        this.updateWordDisplay();
    }

    updateWordDisplay() {
        // create html elements for each word with appropriate styling
        const wordElements = this.currentWords.map((word, index) => {
            if (index < this.typedWords.length) {
                // words already typed (correct/incorrect)
                const isCorrect = this.typedWords[index] === word;
                return `<span class="${isCorrect ? 'correct' : 'incorrect'}">${word}</span>`;
            } 
            else if (index === this.typedWords.length) {
                // current word being typed (with partial match highlighting)
                let className = 'current';
                if (this.currentInput) {
                    className += word.startsWith(this.currentInput) ? ' partial-correct' : ' partial-incorrect';
                }
                return `<span class="${className}">${word}</span>`;
            } 
            else {
                // future words
                return `<span>${word}</span>`;
            }
        });

        // update display and scroll current word into view
        this.wordDisplay.innerHTML = wordElements.join(' ');
        const currentWord = this.wordDisplay.querySelector('.current');
        if (currentWord) {
            currentWord.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }

    setupGame(difficulty) {
        // set difficulty and get settings
        this.currentDifficulty = difficulty;
        const settings = this.difficultySettings[difficulty];
        
        // reset game state
        this.currentWords = this.getRandomWords(settings.wordCount);
        this.timeLeft = settings.time;
        this.typedWords = [];
        this.correctWords = 0;
        this.totalWordsTyped = 0;
        this.isGameActive = false;
        clearInterval(this.timer);

        // update display
        this.wordInput.value = '';
        this.wordInput.disabled = false;
        this.gameSection.style.display = 'block';
        this.results.style.display = 'none';
        this.timeDisplay.textContent = `Ready - ${this.timeLeft}s`;

        // update word display
        this.updateWordDisplay();

        // focus on input field
        this.wordInput.focus();
    }

    startGame() {
        this.isGameActive = true;
        this.startTime = Date.now();
        this.startTimer();
    }

    startTimer() {
        // update time display
        this.timeDisplay.textContent = this.timeLeft;
        
        this.timer = setInterval(() => {
            this.timeLeft--;
            this.timeDisplay.textContent = this.timeLeft;

            // end game when time runs out
            if (this.timeLeft <= 0) {
                this.endGame();
            }
        }, 1000);
    }

    endGame() {
        clearInterval(this.timer);
        this.isGameActive = false;
        this.wordInput.disabled = true;

        // calculate accuracy and wpm
        const timeElapsed = (Date.now() - this.startTime) / 1000;
        const accuracy = this.totalWordsTyped > 0 
            ? Math.round((this.correctWords / this.totalWordsTyped) * 100) 
            : 0;
        const wpm = Math.round((this.correctWords / timeElapsed) * 60);

        // update results display
        this.results.style.display = 'block';
        this.correctWordsDisplay.textContent = `${this.correctWords} (${wpm} WPM)`;
        this.accuracyDisplay.textContent = accuracy;
    }

    resetGame() {
        // hide game and results
        this.gameSection.style.display = 'none';
        this.results.style.display = 'none';

        // reset game state
        this.currentDifficulty = null;
        this.isGameActive = false;
        clearInterval(this.timer);
    }

    getRandomWords(count) {
        // return random words from the list
        return [...this.words].sort(() => Math.random() - 0.5).slice(0, count);
    }
}

const game = new TypeRacer(); 