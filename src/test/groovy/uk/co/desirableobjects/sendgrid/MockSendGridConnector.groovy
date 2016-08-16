package uk.co.desirableobjects.sendgrid

class MockSendGridConnector extends SendGridApiConnectorService {

    Map lastCall = [:]

    @Override
    def post(SendGridEmail email) {
        
        lastCall.method = 'post'
        lastCall.putAll(email.toMap())
    }

    def propertyMissing(String name) { lastCall[name] }

}
