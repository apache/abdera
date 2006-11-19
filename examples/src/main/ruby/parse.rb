#!/usr/bin/env jruby

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  The ASF licenses this file to You
# under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.  For additional information regarding
# copyright in this work, please see the NOTICE file in the top level
# directory of this distribution.

require 'java'

Abdera = org.apache.abdera.Abdera
URL    = java.net.URL

if ARGV.length != 1 then
  STDERR.print <<EOF
Usage: parse.rb <url>
EOF
  exit 1
end

url = URL.new(ARGV[0])

abdera = Abdera.new

parser = abdera.parser

doc = parser.parse(url.open_stream(), url.to_string())

feed = doc.root

print "#{feed.title}\n"

for entry in feed.entries do
  print "  #{entry.title} posted on #{entry.updated}\n"
end
