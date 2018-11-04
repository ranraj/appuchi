package com.yali.domain.repository

import com.yali.domain.model.{Country, CountryState, ID, Language}
import scalikejdbc._

class CountryRepository extends RepositoryHelper {
  val country = Country.syntax("country")

  def findAll(code: String)(implicit session: DBSession): List[Country] =
    sql"""select ${country.result.*} from ${Country.as(country)} where ${country.code} = $code """
      .map(Country(country.resultName)).list.apply()

  def findAll()(implicit session: DBSession): List[Country] =
    sql"""select ${country.result.*} from ${Country.as(country)}"""
      .map(Country(country.resultName)).list.apply()

  def find(countryId: ID)(implicit session: DBSession): Option[Country] =
    sql"""select ${country.result.*} from ${Country.as(country)} where ${country.id} = $countryId """
      .map(Country(country.resultName)).single.apply()

  def create(entity: Country)(implicit session: DBSession): Country = {
    sql"""insert into ${Country.table} (id, name, code, currency, dollar_rate, description) values (
                    ${entity.id},
                    ${entity.name},
                    ${entity.code},
                    ${entity.currency},
                    ${entity.dollarRate},
                    ${entity.description})""".update.apply()
    mustExist(find(entity.id))
  }

  def update(entity: Country)(implicit session: DBSession): Country = {
    sql"""update ${Country.table} set
              name = ${entity.name},
              code=${entity.code},
              currency=${entity.currency},
              dollar_rate=${entity.dollarRate},
              description=${entity.description}
              where id = ${entity.id}""".update.apply()
    mustExist(find(entity.id))
  }

  def delete(countryId: ID)(implicit session: DBSession): Boolean =
    sql"delete from ${Country.table} where id = $countryId".update().apply() > 0
}


class LanguageRepository extends RepositoryHelper {

  val (language, country) = (Language.syntax("language"), Country.syntax("country"))

  def findAll(locale: String)(implicit session: DBSession): List[Language] =
    sql"""select ${language.result.*},${country.result.*} from ${Language.as(language)} left join ${Country.as(country)}
             on ${language.countryId} =${country.id} where ${language.locale} = $locale """
      .map(Language(language.resultName)).list.apply()

  def findAllByCountry(countryId: ID)(implicit session: DBSession): List[Language] =
    sql"""select ${language.result.*} from ${Language.as(language)} left join ${Country.as(country)}
             on ${language.countryId} =${country.id} where ${language.countryId} = $countryId """
      .map(Language(language.resultName)).list.apply()

  def find(countryId: ID,languageId: ID)(implicit session: DBSession): Option[Language] =
    sql"""select ${language.result.*},${country.result.*} from ${Language.as(language)} left join ${Country.as(country)}
             on ${language.countryId} =${country.id} where ${language.id} = $languageId and ${country.id} = $countryId"""
      .map(Language(language.resultName, country.resultName)).single.apply()

  def find(languageId: ID)(implicit session: DBSession): Option[Language] =
    sql"""select ${language.result.*},${country.result.*} from ${Language.as(language)} left join ${Country.as(country)}
             on ${language.countryId} =${country.id} where ${language.id} = $languageId """
      .map(Language(language.resultName, country.resultName)).single.apply()

  def create(countryId: ID,entity: Language)(implicit session: DBSession): Language = {
    sql"""insert into ${Language.table} (id, name, locale, country_id) values (
                    ${entity.id},
                    ${entity.name},
                    ${entity.locale},
                    ${countryId})""".update.apply()
    mustExist(find(countryId,entity.id))
  }

  def update(countryId: ID, entity: Language)(implicit session: DBSession): Language = {
    sql"""update ${Language.table} set
              name = ${entity.name},
              locale=${entity.locale},
              country_id=${entity.countryId} where ${language.countryId} = $countryId""".update.apply()
    mustExist(find(countryId,entity.id))
  }

  def delete(countryId: ID, languageId: ID)(implicit session: DBSession): Boolean =
    sql"delete from ${Language.table} where id = $languageId and ${language.countryId} = $countryId".update().apply() > 0
}

class CountryStateRepository extends RepositoryHelper {

  val (countryState, country) = (CountryState.syntax("language"), Country.syntax("country"))

  def findAll(name: String)(implicit session: DBSession): List[CountryState] =
    sql"""select ${countryState.result.*},${country.result.*} from ${CountryState.as(countryState)} left join ${Country.as(country)}
             on ${countryState.countryId} =${country.id} where ${countryState.name} = $name """
      .map(CountryState(countryState.resultName)).list.apply()

  def find(id: ID)(implicit session: DBSession): Option[CountryState] =
    sql"""select ${countryState.result.*},${country.result.*} from ${CountryState.as(countryState)} left join ${Country.as(country)}
             on ${countryState.countryId} =${country.id} where ${countryState.id} = $id """
      .map(CountryState(countryState.resultName, country.resultName)).single.apply()

  def create(entity: CountryState)(implicit session: DBSession): CountryState = {
    sql"""insert into ${CountryState.table} (id, name, locale, country_id) values (
                    ${entity.id},
                    ${entity.name},
                    ${entity.countryId})""".update.apply()
    mustExist(find(entity.id))
  }

  def update(entity: CountryState)(implicit session: DBSession): CountryState = {
    sql"""update ${CountryState.table} set
              name = ${entity.name},
              country_id=${entity.countryId}""".update.apply()
    mustExist(find(entity.id))
  }

  def delete(id: ID)(implicit session: DBSession): Boolean =
    sql"delete from ${CountryState.table} where id = $id".update().apply() > 0
}

