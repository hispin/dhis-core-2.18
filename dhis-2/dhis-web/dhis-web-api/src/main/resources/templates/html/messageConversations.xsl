<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:messageConversations">
    <h3> <xsl:value-of select="local-name()" /> </h3>

    <h4>Starting a new conversation</h4>
    <p>This is done through a POST to <code>/api/messageConversations using</code> the format below.</p>

  <pre style="font-size: 0.9em;">
  <![CDATA[
  <message>
    <subject>The subject</subject>
    <text>This is the text</text>

    <users>
      <user id="uid1" />
      <user id="uid2" />
      <user id="uid3" />
      <user id="uid4" />
    </users>
  </message>
  ]]>
  </pre>

    <p>
      Example using curl:<br/>
      <code>curl -u admin:district -X POST -H "Content-Type: application/xml" -d @input.xml http://localhost:8080/api/messageConversations</code>
    </p>
    <h4>Replying to a conversation</h4>
    <p>
      This is done with a POST to <code>/api/messageConversations/{uid-of-conversation}?subject=subject</code>. The body of this will be the actual reply.<br/>
      <br/>Example using curl:<br/>
      <code>curl -u admin:district -X POST -H "Content-Type: text/plain" -d "this is a reply" http://localhost:8080/api/messageConversations/adfad134?subject=subject</code>
    </p>

    <h4>Writing a feedback</h4>
    <p>This is done with a POST to <code>/api/messageConversations/feedback</code><br/>
      <br/>Example using curl:<br/>
      <code>curl -u admin:district -X POST -H "Content-Type: text/plain" -d "this is my feedback" http://localhost:8080/api/messageConversations/feedback</code>
    </p>
    
    <table>
      <xsl:apply-templates select="child::*" mode="row" />
    </table>
  </xsl:template>

</xsl:stylesheet>
