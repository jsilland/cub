/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.i18n.pseudolocalization;

import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.methods.Accenter;
import com.google.i18n.pseudolocalization.methods.BracketAdder;
import com.google.i18n.pseudolocalization.methods.Expander;
import com.google.i18n.pseudolocalization.methods.FakeBidi;
import com.google.i18n.pseudolocalization.methods.HtmlPreserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A pipeline for applying zero or more pseudolocalization methods to a message.
 */
public class PseudolocalizationPipeline {

  /**
   * Localize a structured message, mutating it as necessary.
   *
   * @param message message to localize
   */
  public void localize(Message message) {
    for (PseudolocalizationMethod method : pipeline) {
      message.accept(method);
    }
  }

  /**
   * Localize a message with no structure.
   *
   * @param text
   * @return localized text
   */
  public String localize(String text) {
    SimpleMessage message = new SimpleMessage(text);
    localize(message);
    return message.getText();
  }

  /**
   * A factory that creates {@link PseudolocalizationMethod} instances via
   * reflection.
   */
  private static class ReflectiveFactory implements PseudolocalizationMethodFactory {

    /**
     * A constructor taking only a {@link PseudolocalizationMethod}.
     */
    private final Constructor<? extends PseudolocalizationMethod> noArgsCtor;

    /**
     * A constructor taking a {@link PseudolocalizationMethod} and a String
     * argument (which may be null if no arguments are supplied).  This field
     * is null if no such constructor is provided, in which case arguments are
     * not accepted.
     */
    private final Constructor<? extends PseudolocalizationMethod> argsCtor;

    public ReflectiveFactory(Class<? extends PseudolocalizationMethod> methodClass) {
      Constructor<? extends PseudolocalizationMethod> ctor = null;
      Throwable caught = null;
      try {
        ctor = methodClass.getConstructor();
      } catch (SecurityException e) {
        caught = e;
      } catch (NoSuchMethodException e) {
        caught = e;
      }
      if (caught != null) {
        throw new RuntimeException(methodClass
            + " must have a default constructor", caught);
      }
      noArgsCtor = ctor;
      ctor = null;
      try {
        ctor = methodClass.getConstructor(Map.class);
      } catch (SecurityException e) {
        // ignore errors
      } catch (NoSuchMethodException e) {
        // ignore errors
      }
      argsCtor = ctor;
    }

    public PseudolocalizationMethod create(Map<String, String> options) {
      Throwable caught = null;
      try {
        if (options != null && argsCtor != null) {
          return argsCtor.newInstance(options);
        } else {
          return noArgsCtor.newInstance();
        }
      } catch (IllegalArgumentException e) {
        caught = e;
      } catch (InstantiationException e) {
        caught = e;
      } catch (IllegalAccessException e) {
        caught = e;
      } catch (InvocationTargetException e) {
        caught = e;
      }
      throw new RuntimeException("Unable to instantiate " + noArgsCtor.getDeclaringClass(),
          caught);
    }
  }

  static {
    // created here since register methods below will wind up referencing them
    methodRegistry = new HashMap<String, PseudolocalizationMethodFactory>();
    variantRegistry = new HashMap<String, String[]>();

    // register known pseudolocalization methods
    Accenter.register();
    BracketAdder.register();
    Expander.register();
    FakeBidi.register();
    HtmlPreserver.register();

    // regiter known pseudolocalization variants
    registerVariant("psaccent", new String[] { "accents", "expand", "brackets" });
    registerVariant("psbidi", new String[] { "fakebidi" });
  }

  private static Map<String, PseudolocalizationMethodFactory> methodRegistry;

  private static Map<String, String[]> variantRegistry;

  /**
   * Create a pipeline of pseudolocalization methods, optionally preserving HTML
   * tags.
   * @param options 
   * 
   * @param preserveHtml true if the message should be parsed as HTML and
   *     preserve the HTML if present; note that false positives are possible
   * @param methodsWithArgs a list of method names, each with an optional
   *     argument separated by a colon (ie, "foomethod:args")
   * @return the top of the method chain
   * @throws RuntimeException if the creation of any of these methods failed
   */
  public static PseudolocalizationPipeline buildPipeline(Map<String, String> options,
      boolean preserveHtml, List<String> methodsWithArgs) {
    List<PseudolocalizationMethod> chain = new ArrayList<PseudolocalizationMethod>();
    if (preserveHtml) {
      chain.add(new HtmlPreserver());
    }
    for (String methodWithArgs : methodsWithArgs) {
      PseudolocalizationMethod method = createMethod(options, methodWithArgs);
      chain.add(method);
    }
    return new PseudolocalizationPipeline(chain);
  }

  /**
   * Create a pipeline of pseudolocalization methods, optionally preserving HTML
   * tags.
   * 
   * @param preserveHtml true if the message should be parsed as HTML and
   *     preserve the HTML if present; note that false positives are possible
   * @param methodsWithArgs a list of method names, each with an optional
   *     argument separated by a colon (ie, "foomethod:args")
   * @return the top of the method chain
   * @throws RuntimeException if the creation of any of these methods failed
   */
  public static PseudolocalizationPipeline buildPipeline(boolean preserveHtml,
      String... methodsWithArgs) {
    return buildPipeline(null, preserveHtml, Arrays.asList(methodsWithArgs));
  }

  /**
   * Create a pipeline of pseudolocalization methods, optionally preserving HTML
   * tags.
   * 
   * @param options map of options to provide to the pipeline, keys are either
   *    "method:arg" => "value", or some global option with a naming scheme
   *    defined between this caller and the methods used
   * @param preserveHtml true if the message should be parsed as HTML and
   *     preserve the HTML if present; note that false positives are possible
   * @param methodsWithArgs a list of method names, each with an optional
   *     argument separated by a colon (ie, "foomethod:args")
   * @return the top of the method chain
   * @throws RuntimeException if the creation of any of these methods failed
   */
  public static PseudolocalizationPipeline buildPipeline(Map<String, String> options,
      boolean preserveHtml, String... methodsWithArgs) {
    return buildPipeline(options, preserveHtml, Arrays.asList(methodsWithArgs));
  }

  /**
   * Create a pipeline of pseudolocalization methods, preserving HTML tags.
   * 
   * @param methodsWithArgs a list of method names, each with an optional
   *     argument separated by a colon (ie, "foomethod:args")
   * @return the top of the method chain
   * @throws RuntimeException if the creation of any of these methods failed
   */
  public static PseudolocalizationPipeline buildPipeline(List<String> methodsWithArgs) {
    return buildPipeline(null, true, methodsWithArgs);
  }

  /**
   * Create a pipeline of pseudolocalization methods, preserving HTML tags.
   * 
   * @param options map of options to provide to the pipeline, keys are either
   *    "method:arg" => "value", or some global option with a naming scheme
   *    defined between this caller and the methods used
   * @param methodsWithArgs a list of method names, each with an optional
   *     argument separated by a colon (ie, "foomethod:args")
   * @return the top of the method chain
   * @throws RuntimeException if the creation of any of these methods failed
   */
  public static PseudolocalizationPipeline buildPipeline(Map<String, String> options,
      List<String> methodsWithArgs) {
    return buildPipeline(options, true, methodsWithArgs);
  }

  /**
   * Create a pipeline of pseudolocalization methods, preserving HTML tags.
   * 
   * @param methodsWithArgs a list of method names, each with an optional
   *     argument separated by a colon (ie, "foomethod:args")
   * @return the top of the method chain
   * @throws RuntimeException if the creation of any of these methods failed
   */
  public static PseudolocalizationPipeline buildPipeline(String... methodsWithArgs) {
    return buildPipeline(null, true, Arrays.asList(methodsWithArgs));
  }

  /**
   * Create a pipeline of pseudolocalization methods, preserving HTML tags.
   * 
   * @param options map of options to provide to the pipeline, keys are either
   *    "method:arg" => "value", or some global option with a naming scheme
   *    defined between this caller and the methods used
   * @param methodsWithArgs a list of method names, each with an optional
   *     argument separated by a colon (ie, "foomethod:args")
   * @return the top of the method chain
   * @throws RuntimeException if the creation of any of these methods failed
   */
  public static PseudolocalizationPipeline buildPipeline(Map<String, String> options,
      String... methodsWithArgs) {
    return buildPipeline(options, true, Arrays.asList(methodsWithArgs));
  }

  /**
   * Create an instance of the requested method, chaining to the provided
   * method.
   * @param options 
   * 
   * @param methodWithArgs
   * @return a {@link PseudolocalizationMethod} instance, never null
   * @throws RuntimeException if the method is unknown or creation fails
   */
  public static PseudolocalizationMethod createMethod(Map<String, String> options,
      String methodWithArgs) {
    int colon = methodWithArgs.indexOf(':');
    String args = null;
    String methodName = methodWithArgs;
    if (colon >= 0) {
      Map<String, String> newOptions = new HashMap<String, String>();
      if (options != null) {
        newOptions.putAll(options);
      }
      options = newOptions;
      methodName = methodWithArgs.substring(0, colon);
      for (String arg : methodWithArgs.substring(colon + 1).split(":")) {
        String key = arg;
        String value = "";
        int equals = arg.indexOf('=');
        if (equals >= 0) {
          key = arg.substring(0, equals);
          value = arg.substring(equals + 1);
        }
        options.put(methodName + ":" + key, value);
      }
    }
    PseudolocalizationMethodFactory factory = getMethodFactory(methodName);
    if (factory == null) {
      throw new RuntimeException("Unknown method '" + methodName + "'");
    }
    return factory.create(options);
  }

  /**
   * Retrieve the {@link PseudolocalizationMethodFactory} for a given method
   * name.
   * 
   * @param methodName
   * @return {@link PseudolocalizationMethodFactory} instance or null if not
   *     found
   */
  public static synchronized PseudolocalizationMethodFactory getMethodFactory(String methodName) {
    return methodRegistry.get(methodName);
  }

  /**
   * Get the set of registered method names.
   * 
   * @return unmodifiable set of method names
   */
  public static synchronized Set<String> getRegisteredMethods() {
    return Collections.unmodifiableSet(methodRegistry.keySet());
  }

  /**
   * Get the set of registered variant tags.
   * 
   * @return unmodifiable set of variant tags
   */
  public static synchronized Set<String> getRegisteredVariants() {
    return Collections.unmodifiableSet(variantRegistry.keySet());
  }

  /**
   * Return a pipeline associated with a given BCP47 variant tag, which
   * optionally preserves HTML tags and their attributes.
   * 
   * @param preserveHtml true if the message should be parsed as HTML and
   *     preserve the HTML if present; note that false positives are possible
   * @param variant BCP47 variant tag (case insensitive)
   * @return {@link PseudolocalizationMethod} chain or null if {@code variant}
   *     is not registered
   * @throws RuntimeException if building the registered pipeline failed
   */
  public static synchronized PseudolocalizationPipeline getVariantPipeline(boolean preserveHtml,
      String variant) {
    return getVariantPipeline(null, preserveHtml, variant);
  }

  /**
   * Return a pipeline associated with a given BCP47 variant tag, which
   * optionally preserves HTML tags and their attributes.
   * 
   * @param preserveHtml true if the message should be parsed as HTML and
   *     preserve the HTML if present; note that false positives are possible
   * @param variant BCP47 variant tag (case insensitive)
   * @return {@link PseudolocalizationMethod} chain or null if {@code variant}
   *     is not registered
   * @throws RuntimeException if building the registered pipeline failed
   */
  public static synchronized PseudolocalizationPipeline getVariantPipeline(
      Map<String, String> options, boolean preserveHtml, String variant) {
    variant = variant.toLowerCase(Locale.ENGLISH);
    String[] pipeline = variantRegistry.get(variant);
    if (pipeline == null && variant.startsWith("x-")) {
      pipeline = variantRegistry.get(variant.substring(2));
    }
    if (pipeline == null) {
      return null;
    }
    return buildPipeline(options, preserveHtml, pipeline);
  }

  /**
   * Return a pipeline associated with a given BCP47 variant tag, which
   * preserves HTML tags and their attributes.
   * 
   * @param variant BCP47 variant tag (case insensitive)
   * @return {@link PseudolocalizationMethod} chain or null if {@code variant}
   *    is not registered
   * @throws RuntimeException if building the registered pipeline failed
   */
  public static synchronized PseudolocalizationPipeline getVariantPipeline(String variant) {
    return getVariantPipeline(null, true, variant);
  }

  /**
   * Return a pipeline associated with a given BCP47 variant tag, which
   * preserves HTML tags and their attributes.
   * 
   * @param options map of options to provide to the pipeline, keys are either
   *    "method:arg" => "value", or some global option with a naming scheme
   *    defined between this caller and the methods used
   * @param variant BCP47 variant tag (case insensitive)
   * @return {@link PseudolocalizationMethod} chain or null if {@code variant}
   *    is not registered
   * @throws RuntimeException if building the registered pipeline failed
   */
  public static synchronized PseudolocalizationPipeline getVariantPipeline(
      Map<String, String> options, String variant) {
    return getVariantPipeline(options, true, variant);
  }

  /**
   * Register a {@link PseudolocalizationMethod} that will be created by
   * reflectively invoking a constructor taking a
   * {@link PseudolocalizationMethod} instance to chain to, and if it supports
   * receiving arguments it must have a constructor that also takes a String.
   * 
   * @param methodName
   * @param methodClass
   */
  public static void registerMethodClass(String methodName,
      Class<? extends PseudolocalizationMethod> methodClass) {
    registerMethodFactory(methodName, new ReflectiveFactory(methodClass));
  }

  /**
   * Register a new {@link PseudolocalizationMethodFactory}.
   * 
   * @param methodName
   * @param factory
   */
  public static synchronized void registerMethodFactory(String methodName,
      PseudolocalizationMethodFactory factory) {
    methodRegistry.put(methodName, factory);
  }

  /**
   * Add a variant and an associate pipeline to apply.
   *
   * @param variant BCP47 variant tag (case insensitive)
   * @param pipeline a list of methods (with optional arguments) that would be suitable
   *     to pass to {@link #buildPipeline(Map,String[])}
   */
  public static synchronized void registerVariant(String variant, String... pipeline) {
    variantRegistry.put(variant.toLowerCase(Locale.ENGLISH), pipeline);
  }

  /**
   * Returns true if the supplied BCP47 variant should use the source language
   * rather than the translations matching the language tag. This is needed
   * since you want a pseudolocalized string that is readable, yet you also want
   * to use a locale that will be recognized as RTL when using the
   * {@code fakebidi} method.
   * 
   * @return true if the source language should be used instead of any
   *     translations for the locale
   */
  public static boolean useSourceLanguage(String variant) {
    // TODO(jat): generalize this, perhaps letting individual methods
    // indicate this is desired and looking up the pipeline and seeing if
    // any methods want the source language
    return "psbidi".equalsIgnoreCase(variant);
  }

  private final List<PseudolocalizationMethod> pipeline;

  // @VisibleForTesting
  protected PseudolocalizationPipeline(List<PseudolocalizationMethod> pipeline) {
    this.pipeline = pipeline;
  }
}
