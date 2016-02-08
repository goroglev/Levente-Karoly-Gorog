from string import Template

begin_report = Template('''Dear ${customer},

In ${month_name} ${year}, you have charged:
''')

transaction = Template('from ${start_time} to ${end_time}: ${energy_volume} kWh @ ${transaction_fee}')

end_report = Template('''...

Total amount: ${total_cost}

Kind regards,
Your dearest mobility provider,
The Venerable Inertia
''')
