/*
 * Copyright 2005-2010 LAMP/EPFL
 */
// $Id$

package scala.tools.refactoring.tests.sourcegen

import org.junit.Test
import junit.framework.TestCase
import org.junit.Assert._
import scala.tools.refactoring.sourcegen._

@Test
class LayoutTest {

  @Test
  def simpleConcatenation() {
    assertEquals("ab", Fragment("a") ++ Fragment("b") asText)
    assertEquals("abc", Fragment("a") ++ Fragment("b") ++ Fragment("c")  asText)
    assertEquals("ab", Layout("a") ++ Layout("b") asText)
    assertEquals("abc", Layout("a") ++ Fragment("b") ++ Layout("c") asText)
    assertEquals("abc", Fragment("a") ++ Layout("b") ++ Fragment("c") asText)
  }
  
  @Test
  def concatenationsWithEmpty() {
    val N = NoLayout
    val F = EmptyFragment
    
    assertEquals("", N      asText)
    assertEquals("", F      asText)
    assertEquals("", N ++ F asText)
    assertEquals("", N ++ N asText)
    assertEquals("", F ++ N asText)
    assertEquals("", F ++ F asText)
    
    assertEquals("a", Fragment("a") ++ F asText)
    assertEquals("a", Fragment("a") ++ N asText)
    
    assertEquals("a", F ++ Fragment("a") asText)
    assertEquals("a", N ++ Fragment("a")  asText)
    
    assertEquals("ab", Fragment("a") ++ F ++ Layout("b") asText)
    assertEquals("ab", Layout("a") ++ N ++ Fragment("b")asText)
  }
  
  @Test
  def complexConcatenations() {
    val a = Layout("a")
    val b = Layout("b")
    val c = Layout("c")
    
    (Fragment(a, b, c) ++ Fragment(a, b, c)) match {
      case Fragment(a, b, c) =>
        assertEquals("a", a.asText)
        assertEquals("bcab", b.asText)
        assertEquals("c", c.asText)
    }
    
    (Fragment(a, b, c) ++ a) match {
      case Fragment(a, b, c) =>
        assertEquals("a", a.asText)
        assertEquals("b", b.asText)
        assertEquals("ca", c.asText)
    }
    
    (b ++ Fragment(a, b, c)) match {
      case Fragment(a, b, c) =>
        assertEquals("ba", a.asText)
        assertEquals("b", b.asText)
        assertEquals("c", c.asText)
    }
    
    (Fragment("a") ++ Fragment("b")) match {
      case Fragment(a, b, c) =>
        assertEquals("", a.asText)
        assertEquals("ab", b.asText)
        assertEquals("", c.asText)
    }
  }
  
  @Test
  def preserveRequisites() {
    val r = SeparatedBy(",")
    val a = Fragment("a") 
    val b = Fragment("b") 
    val x = Layout("x")
    val y = Layout("y")
    
    assertEquals("a,x", a ++ r ++ x asText)
    assertEquals("a,b", a ++ r ++ b asText)
  }
  
  @Test
  def requisitesAreBetween() {
    val r = SeparatedBy(",")
    val a = Fragment(Layout("a"), Layout("b"), Layout("c"))
    val b = Fragment(Layout("x"), Layout("y"), Layout("z"))
    
    assertEquals("abc,xyz", a ++ r ++ b asText)
  }
  
  @Test
  def requisitesAreOnlyUsesWhenNeeded1() {
    val r = SeparatedBy(",")
    val a = Fragment(Layout("a"), Layout("b"), Layout(","))
    val b = Fragment(Layout("x"), Layout("y"), Layout("z"))
    
    assertEquals("ab,xyz", a ++ r ++ b asText)
  }
  
  @Test
  def requisitesAreOnlyUsesWhenNeeded2() {
    val r = SeparatedBy(",")
    val a = Fragment(Layout("a"), Layout("b"), Layout("c"))
    val b = Fragment(Layout(","), Layout("y"), Layout("z"))
    
    assertEquals("abc,yzabc", a ++ r ++ b ++ a asText)
  }
  
  @Test
  def overlap() {
    assertEquals(0, Layout("abcde") overlap Layout("fghij"))
    assertEquals(0, Layout("abcde") overlap Layout("d"))
    assertEquals(0, Layout("abcde") overlap Layout(""))
    assertEquals(0, Layout("abcde") overlap Layout("x"))
    
    assertEquals(5, Layout("abcde") overlap Layout("abcde"))
    assertEquals(4, Layout("abcde") overlap Layout("bcde"))
    assertEquals(2, Layout("abcde") overlap Layout("defgh"))
    assertEquals(1, Layout("abcde") overlap Layout("e"))
    assertEquals(3, Layout("abcdeee") overlap Layout("eeee"))
  }
}
