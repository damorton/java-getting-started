package com.bitbosh.DropwizardHeroku;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class DropwizardHerokuConfiguration extends Configuration {

  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

  /**
   * A getter for the database factory.
   *
   * @return An instance of database factory deserialized from the configuration
   *         file passed as a command-line argument to the application.
   * @throws URISyntaxException
   */
  @JsonProperty("database")
  public DataSourceFactory getDataSourceFactory() {

    // Overwrite the value read from the database property of the config.yml
    // file and use the value provided by Heroku environment.
    //
    // When running the app locally using `heroku local` the remote postgres
    // db url needs to be added to the .env file.
    // Run `heroku config` and copy `DATABASE_URL: <database-url>` into the
    // .env file.
    //
    // For reference:
    // https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java#connecting-to-a-database-remotely
    URI dbUri = null;
    try {
      dbUri = new URI(System.getenv("DATABASE_URL"));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

    this.database.setUser(username);
    this.database.setPassword(password);
    this.database.setUrl(dbUrl);
    return database;
  }

  /**
   * Needed to set the database factory when the config.yml file is loaded.
   * 
   * @param dataSourceFactory
   */
  @JsonProperty("database")
  public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
    this.database = dataSourceFactory;
  }
}