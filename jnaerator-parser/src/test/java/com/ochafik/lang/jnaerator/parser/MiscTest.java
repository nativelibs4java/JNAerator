/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ochafik.lang.jnaerator.parser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ochafik
 */
public class MiscTest {
    
    @Test
    public void parseMods() {
        assertEquals(ModifierType.UUID, ModifierType.parseModifier("uuid"));//, ModifierKind.Extended));
    }
}
