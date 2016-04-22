import unittest
from app.calculator import Calculator


class TestCalculator(unittest.TestCase):

    def setUp(self):
        self.calc = Calculator()

    def test_calculator_add_method_returns_correct_result(self):
        res = self.calc.add(2, 2)
        self.assertEqual(4, res) 

    def test_calculator_add_method_returns_error_if_args_are_not_numbers(self):
        self.assertRaises(ValueError, self.calc.add, "two", "three")

    def test_calculator_add_method_returns_error_if_one_arg_is_not_number(self):
        self.assertRaises(ValueError, self.calc.add, 'a', 1.2)

if __name__ == "__main__":
    unittest.main()

