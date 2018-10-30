package hudson.tasks._ant;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.Ant;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.SingleFileSCM;
import org.jvnet.hudson.test.ToolInstallations;

import static org.junit.Assert.assertEquals;
/**
 * @author Kohsuke Kawaguchi
 */
public class AntTargetAnnotationTest {
    
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();
    
    private boolean enabled;

    @Before
    public void setUp() {
        enabled = AntTargetNote.ENABLED;
    }

    @After
    public void tearDown() {
        // Restore the original setting.
        AntTargetNote.ENABLED = enabled;
    }

    @Test
    public void test1() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        Ant.AntInstallation ant = ToolInstallations.configureDefaultAnt(tmp);
        p.getBuildersList().add(new Ant("foo", ant.getName(), null, null, null));
        p.setScm(new SingleFileSCM("build.xml", getClass().getResource("simple-build.xml")));
        FreeStyleBuild b = j.buildAndAssertSuccess(p);

        AntTargetNote.ENABLED = true;
        WebClient wc = j.createWebClient();
        HtmlPage c = wc.getPage(b, "console");
        System.out.println(c.asText());
        DomElement o = c.getElementById("console-outline");

        assertEquals(2,o.getByXPath(".//LI").size());
    }
    
}
