package modules

import com.google.inject.AbstractModule

import models.daos.AuthTokenDAO
import models.daos.AuthTokenDAOImpl
import models.services.AuthTokenService
import models.services.AuthTokenServiceImpl
import net.codingwell.scalaguice.ScalaModule

/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    ()
  }
}
