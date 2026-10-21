# Smart Pantry Manager

An Android app (written in Java) that helps cut food waste. You add the ingredients you already have at home, and the app only suggests recipes you can cook with those ingredients. No shopping trip needed.

Built for the Mobile App Development 700 practical assignment.

## What it does

- Add, edit and delete pantry items (name, quantity, unit and an optional expiry date)
- Pantry list screen with swipe-to-delete and an Undo option
- 20 recipes are loaded into the database the first time the app runs
- **Suggested Recipes** screen shows a recipe only if every ingredient is in the pantry in at least the amount needed
- Recipe detail screen with the full ingredient list and method
- Settings screen with a toggle to highlight items that are expiring soon (shown in red)
- Message on the suggestions screen when nothing matches yet
- Bottom navigation between Pantry, Recipes and Settings

## The strict-matching rule

A recipe is only suggested when the pantry covers all of its ingredients. If a recipe needs 5 ingredients and the pantry has 4, it is left out. The logic is in `RecipeMatcher.java` and it handles a few simple real-world differences:

- singular and plural names match (tomato and tomatoes)
- units are converted, so 1 kg counts as 1000 g
- if the same ingredient is added twice, the amounts are added together

Known limitation: it does not convert between weight and volume (for example grams of sugar to tablespoons), because that needs density data. The seeded recipes use one unit per ingredient so this doesn't cause problems.

## Database: Room (SQLite)

I used **Room**, which sits on top of SQLite and stores everything on the device. I chose it because:

- the app doesn't need a server, an internet connection or an account, so it works offline
- Room checks the SQL queries when the project is compiled, so mistakes show up early
- it works with LiveData, so the pantry list updates by itself when the data changes
- the data is saved on the device, so it is still there after closing and reopening the app

Tables: `pantry_items`, `recipes` and `recipe_ingredients` (each ingredient row points to its recipe with `recipeId`).

## Project structure

```
app/src/main/java/com/example/smartpantry/
  data/    Room entities, DAOs, AppDatabase and the recipe seeder
  logic/   RecipeMatcher (strict matching) and AppSettings
  ui/      Activities and adapters (pantry list, add/edit, suggestions, recipe detail, settings)
app/src/test/   Unit tests for RecipeMatcher
```

## How to set up and run

1. Install Android Studio (a recent version).
2. Clone the repository: `git clone https://github.com/BLACKNINJA007/Smart_Pantry.git`
3. In Android Studio choose **File > Open** and select the cloned folder.
4. Wait for the Gradle sync to finish.
5. Create an emulator in **Device Manager** (any Pixel phone, API 24 or higher), or plug in a phone with USB debugging turned on.
6. Press the green **Run** button.

To run the unit tests, right-click `RecipeMatcherTest` in `app/src/test` and choose **Run**.

## Notes

- Minimum SDK: API 24 (Android 7.0)
- No Google Maps, location or GPS features are used
