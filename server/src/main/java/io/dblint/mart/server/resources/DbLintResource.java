package io.dblint.mart.server.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import io.dblint.mart.server.pojo.GitState;
import io.dblint.mart.server.pojo.QueryResponse;
import io.dblint.mart.server.pojo.SqlQuery;
import io.dblint.mart.sqlplanner.planner.Parser;
import org.apache.calcite.sql.SqlDialect;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/api/dblint/")
@Produces(MediaType.APPLICATION_JSON)
public class DbLintResource {
  final Parser parser;
  final GitState gitState;

  public DbLintResource(Parser parser, GitState gitState) {
    this.parser = parser;
    this.gitState = gitState;
  }

  /**
   * Return a digest of the Sql Query.
   * @param sql SqlQuery object with SQL string and other properties
   * @return SQL Digest if successful else an error message
   */
  @POST
  @Path("/digest")
  @Metered
  @ExceptionMetered
  public QueryResponse digest(SqlQuery sql) {
    try {
      String digest = parser.digest(sql.sql,
              SqlDialect.DatabaseProduct.valueOf(sql.dialect.toUpperCase()).getDialect());
      return new QueryResponse(digest, true);
    } catch (Exception exc) {
      return new QueryResponse(exc.getMessage(), false);
    }
  }

  /**
   * Return a pretty of the Sql Query.
   * @param sql SqlQuery object with SQL string and other properties
   * @return Pretty Printed if successful else an error message
   */
  @POST
  @Path("/pretty")
  @Metered
  @ExceptionMetered
  public QueryResponse pretty(SqlQuery sql) {
    try {
      String pretty = parser.pretty(sql.sql,
              SqlDialect.DatabaseProduct.valueOf(sql.dialect.toUpperCase()).getDialect());
      return new QueryResponse(pretty, true);
    } catch (Exception exc) {
      return new QueryResponse(exc.getMessage(), false);
    }
  }

  @GET
  @Path("/version")
  @Metered
  public GitState version() {
    return gitState;
  }

  /**
   * Return a Sql query in translated dialect.
   * @param sql SqlQuery object with SQL string and other properties
   * @param from Original dialect of the Query
   * @param to Final dialect of the Query
   * @return Query in translated dialect
   */
  @GET
  @Path("/translate")
  @Metered
  @ExceptionMetered
  public QueryResponse translate(@QueryParam("sql") SqlQuery sql,
                                 @QueryParam("from") SqlDialect from,
                                 @QueryParam("to") SqlDialect to) {
    try {
      String translatedQuery = parser.translate(sql.sql, from, to);
      return new QueryResponse(translatedQuery, true);
    } catch (Exception exc) {
      return new QueryResponse(exc.getMessage(), false);
    }
  }
}
