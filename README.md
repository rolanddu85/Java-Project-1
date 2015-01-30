# Java-Project-1
Financial asset optimization problem

The scenario:
You work for a financial company/institution that has a variety of assets (bonds & debt for example), but lacks cash. In order to obtain cash to pay it's employees and other expenses, they make a variety of deals to trade assets for cash everyday. There are a variety of asset classes and credit grades, but for simplicity, they are divided into these choices.

Asset Classes:
"c" = Corporate
"m" = Municipal
's" = Soverign

Credit Grades:
"AAA" = Highest grade, best quality
"AA"  = Medium grade
"A"   = Lowest grade, high chance of defaulting

The Problem:
The guys in charge of making these deals for cash do not know (or do not care) how much assets are in the company's inventory. They only care about making as much cash as possible to satisfy a daily quota. If a deal has requirements for certain Asset Classes & Credit Grades, they must be satisfied even if the company inventory has run out of those particular assets by borrowing. 
It is your job to allocate the company assets to best satisfy these deals, and borrow as little as possible. You are provided various CSV text files that contain all of the information.

The Files:
Currently, the file pathes are hardcoded to the User's desktop, so the folder and text files must be on the user's desktop to function correctly. Otherwise, the file paths can be changed in the MainDriver.java file.
- Inventory.txt = The company inventory in the format >> "Asset ID","Total monetary value","Asset Class","Credit Grade"
- Deals.txt = The deals made in the format >> "Deal ID","Total monetary value"
- AssetClassRequirements.txt = The deal's asset class requirements in the format >> "Deal ID","Asset Class","Total monetary value"
- CreditRequirements.txt = The deal's credit grade requirements in the format >> "Deal ID","Credit Grade","Total monetary value"
- BorrowCosts.txt = The costs of borrowing assets in the format >> "Asset Class","Credit Grade","Cost"

Extra Notes:
Higher credit grades can be used to fulfill deals that require lower grades (in the scenario that we run out of the lower grades and don't want to borrow). But different asset classes cannot be used to satisfy asset requirements. For simplification, the "borrow cost" is a value to measure how "costly" it is to borrow that asset (so it is more of a guideline) and does not actually play a role in summing "the 'worth' of the assets we borrow".

My Solution:
I decided to use a greedy algorithm to try and solve this problem by:
- Sort all the deals by highest credit grade first
- Sort borrow costs by lowest cost first
- For each deal, iterate through the list of borrow costs and if the requirements match up, check if that asset is available in the inventory, and allocate if everything passes, otherwise continue through all borrow costs
- Then iterate through the list of borrow costs again, and for the 2nd time, borrow immediately since we have already exhausted the inventory.
- Lastly, write the total borrow costs to a text file.
