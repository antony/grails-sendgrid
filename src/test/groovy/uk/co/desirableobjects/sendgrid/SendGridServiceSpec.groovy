package uk.co.desirableobjects.sendgrid

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(SendGridService)
class SendGridServiceSpec extends Specification {

    private static final String RECIPIENT = 'recipient@example.org'
    private static final String RECIPIENT2 = 'recipient@example.com'
    private static final String BCC = 'bcc@example.net'
    private static final String SENDER = 'sender@example.org'
    private static final String MESSAGE_TEXT = 'This is a test'
    private static final String SUBJECT = 'Hello there'

    void setupSpec() {

        grailsApplication.config.sendgrid.username = 'test'
        grailsApplication.config.sendgrid.password = 'test'

    }

    void 'Send an email'() {

        given:
            SendGridEmail sendGridEmail = new SendGridEmail(to: [RECIPIENT], subject:SUBJECT, body:MESSAGE_TEXT, from:SENDER)
            service.sendGridApiConnectorService = Mock(SendGridApiConnectorService)

        when:
            service.send(sendGridEmail)

        then:
            1 * service.sendGridApiConnectorService.post(sendGridEmail)
            0 * _

    }

    void 'Send a text email using the mail plugin DSL'() {

        given:
            service.sendGridApiConnectorService = Mock(SendGridApiConnectorService)

        when:
            service.sendMail {
                to RECIPIENT
                from SENDER
                subject SUBJECT
                body MESSAGE_TEXT
            }

        then:
            1 * service.sendGridApiConnectorService.post({ SendGridEmail email ->
                email.to == RECIPIENT
                email.from == SENDER
                email.subject == SUBJECT
                email.body == MESSAGE_TEXT
            } as SendGridEmail)
            0 * _

    }

    void 'Send a html email using the mail plugin DSL'() {

        given:
            service.sendGridApiConnectorService = Mock(SendGridApiConnectorService)

        when:
            service.sendMail {
                to RECIPIENT
                from SENDER
                bcc BCC
                subject SUBJECT
                html MESSAGE_TEXT
            }

        then:
            1 * service.sendGridApiConnectorService.post({ SendGridEmail email ->
                email.to == RECIPIENT
                email.from == SENDER
                email.subject == SUBJECT
                email.html == MESSAGE_TEXT
                email.bcc == [BCC]
            } as SendGridEmail)
            0 * _

    }

    void 'Send a text email with multiple recipients'() {

        given:
            service.sendGridApiConnectorService = Mock(SendGridApiConnectorService)

        when:
            service.sendMail {
                to RECIPIENT, RECIPIENT2
                from SENDER
                subject SUBJECT
                body MESSAGE_TEXT
            }

        then:
            1 * service.sendGridApiConnectorService.post({ SendGridEmail email ->
                email.to == [RECIPIENT, RECIPIENT2]
                email.from == SENDER
                email.subject == SUBJECT
                email.body == MESSAGE_TEXT
            } as SendGridEmail)
            0 * _

    }

    void 'Send an email with attachments'() {

        given:
            service.sendGridApiConnectorService = Mock(SendGridApiConnectorService)

        when:
            service.sendMail {
                to RECIPIENT
                from SENDER
                subject SUBJECT
                body MESSAGE_TEXT
                attach new File('./test/unit/true.png')
            }

        then:
            1 * service.sendGridApiConnectorService.post({ SendGridEmail email ->
                email.to == [RECIPIENT, RECIPIENT2]
                email.from == SENDER
                email.subject == SUBJECT
                email.body == MESSAGE_TEXT
                email.attachments = ['true.png': new File('./test/unit/true.png')]
            } as SendGridEmail)
            0 * _

    }

    void 'Send an email with specific api credentials'() {

        given:
            service.sendGridApiConnectorService = Mock(SendGridApiConnectorService)

        when:
            service.sendMail {
                to RECIPIENT
                from SENDER
                subject SUBJECT
                body MESSAGE_TEXT
                apiCredentials 'testuser', 'testpassword'
            }

        then:
            1 * service.sendGridApiConnectorService.post({ SendGridEmail email ->
                email.to == [RECIPIENT, RECIPIENT2]
                email.from == SENDER
                email.subject == SUBJECT
                email.body == MESSAGE_TEXT
                email.username == 'testuser'
                email.password == 'testpassword'
            } as SendGridEmail)
            0 * _

    }

}
