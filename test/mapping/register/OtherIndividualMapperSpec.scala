/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.register

import java.time.LocalDate
import base.SpecBase
import generators.Generators
import models.{AddressType, FullName, IdentificationType, InternationalAddress, OtherIndividualType, PassportOrIdCardDetails, PassportType, UkAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.individual._
import pages.register.individual.mld5._
import utils.Constants._

class OtherIndividualMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val mapper = injector.instanceOf[OtherIndividualMapper]
  private val index0 = 0
  private val index1 = 1
  private val firstName = "first"
  private val lastName = "last"
  private val nino = "AB123456C"
  private val dateOfBirth = LocalDate.of(2000,1,1)
  private val passportExpiry = LocalDate.of(2025,1,1)

  "OtherIndividualMapper" when {

    "when user answers is empty" must {

      "must return None" in {

        val userAnswers = emptyUserAnswers

        mapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "return mapped data" when {

        "nino is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), false).success.value
              .set(NationalInsuranceYesNoPage(index0), true).success.value
              .set(NationalInsuranceNumberPage(index0), nino).success.value

          val otherIndividuals = mapper.build(userAnswers)

          otherIndividuals mustBe defined
          otherIndividuals.value.head mustBe OtherIndividualType(
            name = FullName(firstName, None, lastName),
            dateOfBirth = None,
            identification = Some(IdentificationType(nino = Some(nino), address = None, passport = None)),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }

        "UK Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), false).success.value
              .set(NationalInsuranceYesNoPage(index0), false).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUkYesNoPage(index0), true).success.value
              .set(UkAddressPage(index0), UkAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
              .set(PassportDetailsYesNoPage(index0), false).success.value
              .set(IDCardDetailsYesNoPage(index0), false).success.value

          val otherIndividuals = mapper.build(userAnswers)

          otherIndividuals mustBe defined
          otherIndividuals.value.head mustBe OtherIndividualType(
            name = FullName(firstName, None, lastName),
            identification = Some(IdentificationType(nino = None,
              address = Some(AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), "GB")),
              passport = None
            )),
            dateOfBirth = None,
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }

        "International Address is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), false).success.value
              .set(NationalInsuranceYesNoPage(index0), false).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUkYesNoPage(index0), false).success.value
              .set(NonUkAddressPage(index0), InternationalAddress("Line1", "Line2", Some("Line3"), "US")).success.value
              .set(PassportDetailsYesNoPage(index0), false).success.value
              .set(IDCardDetailsYesNoPage(index0), false).success.value

          val otherIndividuals = mapper.build(userAnswers)

          otherIndividuals mustBe defined
          otherIndividuals.value.head mustBe OtherIndividualType(
            name = FullName(firstName, None, lastName),
            identification = Some(IdentificationType(
              nino = None,
              address = Some(AddressType("Line1", "Line2", Some("Line3"), None, None, "US")),
              passport = None
            )),
            dateOfBirth = None,
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }

        "DateOfBirth, Address and Passport is set" in {
          val userAnswers =
            emptyUserAnswers
              .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
              .set(DateOfBirthYesNoPage(index0), true).success.value
              .set(DateOfBirthPage(index0), dateOfBirth).success.value
              .set(NationalInsuranceYesNoPage(index0), false).success.value
              .set(AddressYesNoPage(index0), true).success.value
              .set(AddressUkYesNoPage(index0), true).success.value
              .set(UkAddressPage(index0), UkAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
              .set(PassportDetailsYesNoPage(index0), true).success.value
              .set(PassportDetailsPage(index0), PassportOrIdCardDetails("GB", "12345", passportExpiry)).success.value
              .set(IDCardDetailsYesNoPage(index0), false).success.value

          val otherIndividuals = mapper.build(userAnswers)

          otherIndividuals mustBe defined
          otherIndividuals.value.head mustBe OtherIndividualType(
            name = FullName(firstName, None, lastName),
            identification = Some(IdentificationType(nino = None,
              address = Some(AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), "GB")),
              passport = Some(PassportType("12345", passportExpiry, "GB"))
            )),
            dateOfBirth = Some(dateOfBirth),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        }
      }

      "must be able to create multiple Individual OtherIndividuals" in {
        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), FullName("Individual Name 1", None, lastName)).success.value
            .set(DateOfBirthYesNoPage(index0), false).success.value
            .set(NationalInsuranceYesNoPage(index0), true).success.value
            .set(NationalInsuranceNumberPage(index0), nino).success.value

            .set(NamePage(index1), FullName("Individual Name 2", None, lastName)).success.value
            .set(DateOfBirthYesNoPage(index1), false).success.value
            .set(NationalInsuranceYesNoPage(index1), false).success.value
            .set(AddressYesNoPage(index1), true).success.value
            .set(AddressUkYesNoPage(index1), true).success.value
            .set(UkAddressPage(index1),
              UkAddress("Line1", "Line2", Some("Line3"), Some("Newcastle"), "NE62RT")).success.value
            .set(PassportDetailsYesNoPage(index1), false).success.value
            .set(IDCardDetailsYesNoPage(index1), false).success.value


        val otherIndividuals = mapper.build(userAnswers)

        otherIndividuals mustBe defined
        otherIndividuals.value mustBe
          List(
            OtherIndividualType(
              name = FullName("Individual Name 1", None, lastName),
              dateOfBirth = None,
              identification = Some(IdentificationType(nino = Some(nino), address = None, passport = None)),
              countryOfResidence = None,
              nationality = None,
              legallyIncapable = None
            ),

            OtherIndividualType(
              name = FullName("Individual Name 2", None, lastName),
              dateOfBirth = None,
              identification = Some(
                IdentificationType(
                  nino = None,
                  address = Some(AddressType("Line1", "Line2", Some("Line3"), Some("Newcastle"), Some("NE62RT"), "GB")),
                  passport = None
                )
              ),
              countryOfResidence = None,
              nationality = None,
              legallyIncapable = None
            )
          )
      }


      "must not be able to create OtherIndividualType when incomplete data " in {
        val userAnswers =
          emptyUserAnswers
            .set(DateOfBirthYesNoPage(index0), false).success.value

        mapper.build(userAnswers) mustNot be(defined)
      }

      "In 5mld mode with UK country of residence, UK country of nationality and Mental Capacity" in {

        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
            .set(DateOfBirthYesNoPage(index0), true).success.value
            .set(DateOfBirthPage(index0), dateOfBirth).success.value
            .set(CountryOfNationalityYesNoPage(index0), true).success.value
            .set(CountryOfNationalityInTheUkYesNoPage(index0), true).success.value
            .set(NationalInsuranceYesNoPage(index0), true).success.value
            .set(NationalInsuranceNumberPage(index0), nino).success.value
            .set(CountryOfResidenceYesNoPage(index0), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), true).success.value
            .set(MentalCapacityYesNoPage(index0), true).success.value

        val otherIndividuals = mapper.build(userAnswers)

        otherIndividuals mustBe defined
        otherIndividuals.value.head mustBe OtherIndividualType(
          name = FullName(firstName, None, lastName),
          dateOfBirth = Some(dateOfBirth),
          identification = Some(IdentificationType(nino = Some(nino), address = None, passport = None)),
          countryOfResidence = Some(GB),
          nationality = Some(GB),
          legallyIncapable = Some(false)
        )

      }

      "In 5mld mode with Non UK country of residence, Non UK country of nationality and No Mental Capacity" in {

        val userAnswers =
          emptyUserAnswers
            .set(NamePage(index0), FullName(firstName, None, lastName)).success.value
            .set(DateOfBirthYesNoPage(index0), true).success.value
            .set(DateOfBirthPage(index0), dateOfBirth).success.value
            .set(CountryOfNationalityYesNoPage(index0), true).success.value
            .set(CountryOfNationalityInTheUkYesNoPage(index0), false).success.value
            .set(CountryOfNationalityPage(index0), ES).success.value
            .set(NationalInsuranceYesNoPage(index0), true).success.value
            .set(NationalInsuranceNumberPage(index0), nino).success.value
            .set(CountryOfResidenceYesNoPage(index0), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index0), false).success.value
            .set(CountryOfResidencePage(index0), ES).success.value
            .set(MentalCapacityYesNoPage(index0), false).success.value

        val otherIndividuals = mapper.build(userAnswers)

        otherIndividuals mustBe defined
        otherIndividuals.value.head mustBe OtherIndividualType(
          name = FullName(firstName, None, lastName),
          dateOfBirth = Some(dateOfBirth),
          identification = Some(IdentificationType(nino = Some(nino), address = None, passport = None)),
          countryOfResidence = Some(ES),
          nationality = Some(ES),
          legallyIncapable = Some(true)
        )

      }

    }
  }
}

