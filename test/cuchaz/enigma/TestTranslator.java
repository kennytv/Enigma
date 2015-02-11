package cuchaz.enigma;

import static cuchaz.enigma.TestEntryFactory.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarFile;

import org.junit.BeforeClass;
import org.junit.Test;

import cuchaz.enigma.mapping.Entry;
import cuchaz.enigma.mapping.Mappings;
import cuchaz.enigma.mapping.MappingsReader;
import cuchaz.enigma.mapping.TranslationDirection;
import cuchaz.enigma.mapping.Translator;


public class TestTranslator {

	private static Deobfuscator m_deobfuscator;
	private static Mappings m_mappings;
	private static Translator m_deobfTranslator;
	private static Translator m_obfTranslator;
	
	@BeforeClass
	public static void beforeClass()
	throws Exception {
		m_deobfuscator = new Deobfuscator(new JarFile("build/testTranslation.obf.jar"));
		try (InputStream in = TestTranslator.class.getResourceAsStream("/cuchaz/enigma/resources/translation.mappings")) {
			m_mappings = new MappingsReader().read(new InputStreamReader(in));
			m_deobfuscator.setMappings(m_mappings);
			m_deobfTranslator = m_deobfuscator.getTranslator(TranslationDirection.Deobfuscating);
			m_obfTranslator = m_deobfuscator.getTranslator(TranslationDirection.Obfuscating);
		}
	}
	
	@Test
	public void basicClasses() {
		assertMapping(newClass("none/a"), newClass("deobf/A_Basic"));
		assertMapping(newClass("none/b"), newClass("deobf/B_BaseClass"));
		assertMapping(newClass("none/c"), newClass("deobf/C_SubClass"));
	}

	@Test
	public void basicFields() {
		assertMapping(newField("none/a", "a", "I"), newField("deobf/A_Basic", "f1", "I"));
		assertMapping(newField("none/a", "a", "F"), newField("deobf/A_Basic", "f2", "F"));
		assertMapping(newField("none/a", "a", "Ljava/lang/String;"), newField("deobf/A_Basic", "f3", "Ljava/lang/String;"));
	}
	
	@Test
	public void basicMethods() {
		assertMapping(newMethod("none/a", "a", "()V"), newMethod("deobf/A_Basic", "m1", "()V"));
		assertMapping(newMethod("none/a", "a", "()I"), newMethod("deobf/A_Basic", "m2", "()I"));
		assertMapping(newMethod("none/a", "a", "(I)V"), newMethod("deobf/A_Basic", "m3", "(I)V"));
		assertMapping(newMethod("none/a", "a", "(I)I"), newMethod("deobf/A_Basic", "m4", "(I)I"));
	}
	
	// TODO: basic constructors
	
	@Test
	public void inheritanceFields() {
		assertMapping(newField("none/b", "a", "I"), newField("deobf/B_BaseClass", "f1", "I"));
		assertMapping(newField("none/b", "a", "C"), newField("deobf/B_BaseClass", "f2", "C"));
		assertMapping(newField("none/c", "b", "I"), newField("deobf/C_SubClass", "f3", "I"));
		assertMapping(newField("none/c", "c", "I"), newField("deobf/C_SubClass", "f4", "I"));
	}
	
	@Test
	public void inheritanceFieldsShadowing() {
		assertMapping(newField("none/c", "b", "C"), newField("deobf/C_SubClass", "f2", "C"));
	}
	
	@Test
	public void inheritanceFieldsBySubClass() {
		assertMapping(newField("none/c", "a", "I"), newField("deobf/C_SubClass", "f1", "I"));
		// NOTE: can't reference b.C by subclass since it's shadowed
	}
	
	@Test
	public void inheritanceMethods() {
		assertMapping(newMethod("none/b", "a", "()I"), newMethod("deobf/B_BaseClass", "m1", "()I"));
		assertMapping(newMethod("none/b", "b", "()I"), newMethod("deobf/B_BaseClass", "m2", "()I"));
		assertMapping(newMethod("none/c", "c", "()I"), newMethod("deobf/C_SubClass", "m3", "()I"));
	}
	
	@Test
	public void inheritanceMethodsOverrides() {
		assertMapping(newMethod("none/c", "a", "()I"), newMethod("deobf/C_SubClass", "m1", "()I"));
	}
	
	@Test
	public void inheritanceMethodsBySubClass() {
		assertMapping(newMethod("none/c", "b", "()I"), newMethod("deobf/C_SubClass", "m2", "()I"));
	}
	
	private void assertMapping(Entry obf, Entry deobf) {
		assertThat(m_deobfTranslator.translateEntry(obf), is(deobf));
		assertThat(m_obfTranslator.translateEntry(deobf), is(obf));
	}
}