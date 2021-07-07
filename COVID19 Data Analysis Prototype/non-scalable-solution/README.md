<h1 align="center">Welcome to the COVID-19 Data Analysis application 👋</h1>
<p>
</p>


> COVID19 Data Analysis (CDA) is written in PSQL and Java with a terminal-based interface. This system is used to track information about how COVID-19 will spread; its percentages of positive, negative and inconclusive results in regions, countries, etc; predicting trends with live data, and much more.

## Author

👤 **Dan Murphy**

* Website: https://www.linkedin.com/in/cs-dan-murphy/
* Github: [@pseudodan](https://github.com/pseudodan)

## Functionality ##

```markdown
- [x] Largest number of positive, negative or inconclusive case outcomes by state
- [x] Largest number of tests administered in a specified stated
- [x] Largest number of positive, negative or inconclusive case outcomes in an ordered list of dates
- [x] Largest number of positive, negative or inconclusive cases in a specified date range
- [x] List the number of positive, negative or inconclusive cases per state
- [ ] Calculate the percentage of positive, negative or inconclusive in a specified state in comparison to the remainder of the US
- [ ] Predict the number of confirmed cases in a specified ctate for the following month.
- [ ] Predict the number of tests given in a specified ctate for the following month.
```

## Testing The Application

CDA requires [PSQL](https://www.postgresql.org/download/) and a [JRE](https://www.java.com/en/download/) to run.

Clone the project [here](https://github.com/pseudodan/COVID19-Data-Analysis.git) and enter its respective directory.

Install the dependencies prior to running the application.

Next, we need to start and create a PSQL instance.

```sh
$ cd postgresql
$ source startPostgreSQL.sh
$ source createPostgreSQL.sh
```

Now that the server instance is started and created, proceed to enter the ```java/src``` dir. From your pwd, run the following:

```sh
$ cd ..
$ cd java/src
$ source compile.sh
```

The application will now be running in your terminal with a text-based menu. 



***
