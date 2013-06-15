// Copyright (c) 2011 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#ifndef COMPONENTS_AUTOFILL_BROWSER_AUTOFILL_FIELD_H_
#define COMPONENTS_AUTOFILL_BROWSER_AUTOFILL_FIELD_H_

#include <string>

#include "base/basictypes.h"
#include "base/strings/string16.h"
#include "components/autofill/browser/field_types.h"
#include "components/autofill/shared/form_field_data.h"

namespace autofill {

class AutofillField : public FormFieldData {
 public:
  enum PhonePart {
    IGNORED = 0,
    PHONE_PREFIX = 1,
    PHONE_SUFFIX = 2,
  };

  AutofillField();
  AutofillField(const FormFieldData& field, const base::string16& unique_name);
  virtual ~AutofillField();

  const base::string16& unique_name() const { return unique_name_; }

  const std::string& section() const { return section_; }
  AutofillFieldType heuristic_type() const { return heuristic_type_; }
  AutofillFieldType server_type() const { return server_type_; }
  const FieldTypeSet& possible_types() const { return possible_types_; }
  PhonePart phone_part() const { return phone_part_; }

  // Sets the heuristic type of this field, validating the input.
  void set_section(const std::string& section) { section_ = section; }
  void set_heuristic_type(AutofillFieldType type);
  void set_server_type(AutofillFieldType type);
  void set_possible_types(const FieldTypeSet& possible_types) {
    possible_types_ = possible_types;
  }
  void set_phone_part(PhonePart part) { phone_part_ = part; }

  // This function automatically chooses between server and heuristic autofill
  // type, depending on the data available.
  AutofillFieldType type() const;

  // Returns true if the value of this field is empty.
  bool IsEmpty() const;

  // The unique signature of this field, composed of the field name and the html
  // input type in a 32-bit hash.
  std::string FieldSignature() const;

  // Returns true if the field type has been determined (without the text in the
  // field).
  bool IsFieldFillable() const;

  void set_default_value(const std::string& value) { default_value_ = value; }
  const std::string& default_value() const { return default_value_; }

 private:
  // The unique name of this field, generated by Autofill.
  base::string16 unique_name_;

  // The unique identifier for the section (e.g. billing vs. shipping address)
  // that this field belongs to.
  std::string section_;

  // The type of the field, as determined by the Autofill server.
  AutofillFieldType server_type_;

  // The type of the field, as determined by the local heuristics.
  AutofillFieldType heuristic_type_;

  // The set of possible types for this field.
  FieldTypeSet possible_types_;

  // Used to track whether this field is a phone prefix or suffix.
  PhonePart phone_part_;

  // The default value returned by the Autofill server.
  std::string default_value_;

  DISALLOW_COPY_AND_ASSIGN(AutofillField);
};

}  // namespace autofill

#endif  // COMPONENTS_AUTOFILL_BROWSER_AUTOFILL_FIELD_H_
