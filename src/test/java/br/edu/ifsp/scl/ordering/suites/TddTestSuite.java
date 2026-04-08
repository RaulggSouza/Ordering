package br.edu.ifsp.scl.ordering.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("br.edu.ifsp.scl.ordering")
@IncludeTags("TDD")
public class TddTestSuite {
}

